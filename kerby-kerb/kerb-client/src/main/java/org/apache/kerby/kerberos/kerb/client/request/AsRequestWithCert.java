/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.KOptions;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.crypto.dh.DhClient;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.CMSMessageType;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitCrypto;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPlgCryptoContext;
import org.apache.kerby.kerberos.kerb.spec.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.spec.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.spec.kdc.KdcOption;
import org.apache.kerby.kerberos.kerb.spec.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.spec.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.spec.pa.PaData;
import org.apache.kerby.kerberos.kerb.spec.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.spec.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.spec.pa.pkinit.DHNonce;
import org.apache.kerby.kerberos.kerb.spec.pa.pkinit.DHRepInfo;
import org.apache.kerby.kerberos.kerb.spec.pa.pkinit.KdcDHKeyInfo;
import org.apache.kerby.kerberos.kerb.spec.pa.pkinit.PaPkAsRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;

import javax.crypto.interfaces.DHPublicKey;
import java.io.IOException;
import java.math.BigInteger;

public class AsRequestWithCert extends AsRequest {

    private static final Logger LOG = LoggerFactory.getLogger(AsRequestWithCert.class);
    public static final String ANONYMOUS_PRINCIPAL = "ANONYMOUS@WELLKNOWN:ANONYMOUS";

    public AsRequestWithCert(KrbContext context) {
        super(context);

        setAllowedPreauth(PaDataType.PK_AS_REQ);
        getKdcOptions().setFlag(KdcOption.REQUEST_ANONYMOUS);
    }

    @Override
    public void process() throws KrbException {
        KdcReqBody body = getReqBody();
        AsReq asReq = new AsReq();
        asReq.setReqBody(body);
        setKdcReq(asReq);

        preauth();

        asReq.setPaData(getPreauthContext().getOutputPaData());
        setKdcReq(asReq);
    }

    @Override
    public KOptions getPreauthOptions() {
        KOptions results = new KOptions();

        KOptions krbOptions = getKrbOptions();
        results.add(krbOptions.getOption(KrbOption.PKINIT_X509_CERTIFICATE));
        results.add(krbOptions.getOption(KrbOption.PKINIT_X509_ANCHORS));
        results.add(krbOptions.getOption(KrbOption.PKINIT_X509_PRIVATE_KEY));
        results.add(krbOptions.getOption(KrbOption.PKINIT_X509_IDENTITY));
        results.add(krbOptions.getOption(KrbOption.PKINIT_USING_RSA));

        return results;
    }

    @Override
    public void processResponse(KdcRep kdcRep) throws KrbException  {

        PaData paData = kdcRep.getPaData();
        for (PaDataEntry paEntry : paData.getElements()) {
            // Parse PA-PK-AS-REP message.
            if (paEntry.getPaDataType() == PaDataType.PK_AS_REP) {
                LOG.info("processing PK_AS_REP");

                PaPkAsRep paPkAsRep = KrbCodec.decode(paEntry.getPaDataValue(), PaPkAsRep.class);

                DHRepInfo dhRepInfo =paPkAsRep.getDHRepInfo();

                DHNonce nonce = dhRepInfo.getServerDhNonce();
                byte[] dhSignedData = dhRepInfo.getDHSignedData();
                PKCS7 pkcs7 = null;
                try {
                    pkcs7 = PkinitCrypto.verifyCMSSignedData(
                            CMSMessageType.CMS_SIGN_SERVER, dhSignedData);
                } catch (IOException e) {
                    LOG.error("failed to verify pkcs7 signed data\n");
                }

                if (!PkinitCrypto.verifyKdcSan(getContext().getConfig().getPkinitKdcHostName(),
                pkcs7.getCertificates())) {
                    LOG.error("Did not find an acceptable SAN in KDC certificate");
                }


                ContentInfo contentInfo = pkcs7.getContentInfo();

                LOG.info("as_rep: DH key transport algorithm");
                KdcDHKeyInfo kdcDHKeyInfo;
                try {
                    kdcDHKeyInfo = KrbCodec.decode(contentInfo.getContentBytes(), KdcDHKeyInfo.class);
                } catch (IOException e) {
                    String errMessage = "failed to decode AuthPack " + e.getMessage();
                    LOG.error(errMessage);
                    throw new KrbException(errMessage);
                }

                byte[] subjectPublicKey = kdcDHKeyInfo.getSubjectPublicKey().getValue();

                Asn1Integer clientPubKey = KrbCodec.decode(subjectPublicKey, Asn1Integer.class);
                BigInteger y = clientPubKey.getValue();

                DhClient client = getDhClient();
                BigInteger p = client.getDhParam().getP();
                BigInteger g = client.getDhParam().getG();

                DHPublicKey dhPublicKey = PkinitCrypto.createDHPublicKey(p, g, y);

                EncryptionKey secretKey = null;
                try {
                    client.doPhase(dhPublicKey.getEncoded());
                    secretKey = client.generateKey(null, null, getEncType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Set the DH shared key as the client key
                if(secretKey == null) {
                    throw new KrbException("Fail to create client key.");
                } else {
                    setClientKey(secretKey);
                }
            }
        }
        super.processResponse(kdcRep);
    }
}
