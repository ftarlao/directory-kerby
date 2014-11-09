package org.haox.kerb.client.preauth;

import org.haox.kerb.client.KrbContext;
import org.haox.kerb.client.KrbOption;
import org.haox.kerb.preauth.PaFlags;
import org.haox.kerb.preauth.Preauth;
import org.haox.kerb.spec.KrbException;
import org.haox.kerb.spec.type.common.EncryptionType;
import org.haox.kerb.spec.type.pa.PaData;
import org.haox.kerb.spec.type.pa.PaDataType;

import java.util.List;

public interface KrbPreauth extends Preauth {

    public String getName();

    public void init(KrbContext context);

    /**
     * Get supported encryption types
     */
    public List<EncryptionType> getEncTypes();

    /**
     * Set krb options passed from user
     */
    public void setOptions(List<KrbOption> options);

    /**
     * When first request to server, any paData to provide?
     */
    public void tryFirst(PreauthContext preauthContext, PaData paData) throws KrbException;

    /**
     * Process server returned paData and return back any result paData
     */
    public void process(PreauthContext preauthContext, PaData paData) throws KrbException;

    /**
     * When another request to server in the 4 pass, any paData to provide?
     */
    public void tryAgain(PreauthContext preauthContext, PaData paData);

    /**
     * Return PA_REAL if pa_type is a real preauthentication type or PA_INFO if it is
     * an informational type.
     */
    public PaFlags getFlags(PreauthContext preauthContext, PaDataType paType);

    /**
     * When exiting...
     */
    public void destroy();

}
