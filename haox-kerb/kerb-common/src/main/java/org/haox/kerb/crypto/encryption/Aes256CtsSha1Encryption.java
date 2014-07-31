package org.haox.kerb.crypto.encryption;

import org.haox.kerb.spec.type.common.CheckSumType;
import org.haox.kerb.spec.type.common.EncryptionType;

public class Aes256CtsSha1Encryption extends AesCtsSha1Encryption
{
    public EncryptionType getEncryptionType()
    {
        return EncryptionType.AES256_CTS_HMAC_SHA1_96;
    }


    public CheckSumType checksumType()
    {
        return CheckSumType.HMAC_SHA1_96_AES256;
    }


    public int getKeyLength()
    {
        return 256;
    }
}
