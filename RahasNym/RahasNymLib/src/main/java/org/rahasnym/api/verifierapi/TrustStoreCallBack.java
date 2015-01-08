package org.rahasnym.api.verifierapi;

import org.rahasnym.api.RahasNymException;

import java.security.cert.Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/4/15
 * Time: 10:58 PM
 */

/**
 * The application using the verifier API should implement this callback and register it in VerifierCallBackManager
 * for the API to retrieve the certificate from the verifier application trust store in order to verify the signature on
 * the IDTs.
 */
public interface TrustStoreCallBack {

    public Certificate getCertificateFromAlias(String certAlias) throws RahasNymException;

}
