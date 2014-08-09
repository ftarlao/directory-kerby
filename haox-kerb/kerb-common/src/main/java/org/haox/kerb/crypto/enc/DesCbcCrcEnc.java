package org.haox.kerb.crypto.enc;

import org.haox.kerb.crypto.cksum.provider.Crc32Provider;
import org.haox.kerb.spec.type.common.CheckSumType;
import org.haox.kerb.spec.type.common.EncryptionType;

public class DesCbcCrcEnc extends DesCbcEnc {

    public DesCbcCrcEnc() {
        super(new Crc32Provider());
    }

    public EncryptionType eType() {
        return EncryptionType.DES_CBC_CRC;
    }

    public CheckSumType checksumType() {
        return CheckSumType.CRC32;
    }
}
