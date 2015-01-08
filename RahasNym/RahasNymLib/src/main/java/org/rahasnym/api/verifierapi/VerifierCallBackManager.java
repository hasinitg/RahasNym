package org.rahasnym.api.verifierapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/4/15
 * Time: 10:57 PM
 */

import org.rahasnym.api.RahasNymException;

import java.security.cert.Certificate;

/**
 * This maintains the registered callbacks implemented by the application and performs the operations requested by the
 * API on those callbacks.
 */
public class VerifierCallBackManager {
    private static TrustStoreCallBack trustStoreCallBack;

    /*Since the call back registration happens at the initialization of the application, no need to go for a getInstance method.*/
    public static void registerTrustStoreCallBack(TrustStoreCallBack tsCallBack){
        trustStoreCallBack = tsCallBack;
        return;
    }

    public static Certificate getTrustedCert(String alias) throws RahasNymException {
        return trustStoreCallBack.getCertificateFromAlias(alias);
    }

}
