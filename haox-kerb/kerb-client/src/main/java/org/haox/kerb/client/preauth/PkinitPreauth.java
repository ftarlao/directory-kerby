package org.haox.kerb.client.preauth;

import org.haox.kerb.client.KrbContext;
import org.haox.kerb.client.KrbOption;
import org.haox.kerb.common.EncryptionUtil;
import org.haox.kerb.preauth.PaFlag;
import org.haox.kerb.preauth.PaFlags;
import org.haox.kerb.preauth.PkinitPreauthBase;
import org.haox.kerb.preauth.TimestampPreauthBase;
import org.haox.kerb.spec.KrbException;
import org.haox.kerb.spec.type.common.EncryptedData;
import org.haox.kerb.spec.type.common.EncryptionType;
import org.haox.kerb.spec.type.common.KeyUsage;
import org.haox.kerb.spec.type.pa.PaData;
import org.haox.kerb.spec.type.pa.PaDataEntry;
import org.haox.kerb.spec.type.pa.PaDataType;
import org.haox.kerb.spec.type.pa.PaEncTsEnc;

import java.util.Collections;
import java.util.List;

public class PkinitPreauth extends PkinitPreauthBase implements KrbPreauth {

    private KrbContext context;

    public void init(KrbContext context) {
        this.context = context;
    }

    @Override
    public List<EncryptionType> getEncTypes() {
        return Collections.emptyList();
    }

    @Override
    public void setOptions(List<KrbOption> options) {

    }

    @Override
    public void tryFirst(PreauthContext preauthContext, PaData paData) throws KrbException {

    }

    @Override
    public void process(PreauthContext preauthContext, PaData paData) throws KrbException {

    }

    @Override
    public void tryAgain(PreauthContext preauthContext, PaData paData) {

    }

    @Override
    public PaFlags getFlags(PreauthContext preauthContext, PaDataType paType) {
        PaFlags paFlags = new PaFlags(0);
        paFlags.setFlag(PaFlag.PA_REAL);

        return paFlags;
    }

    @Override
    public void destroy() {

    }

}
