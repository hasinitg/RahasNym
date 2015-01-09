package org.rahasnym.serviceprovider;

import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.verifierapi.TrustStoreCallBack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/5/15
 * Time: 12:44 AM
 */

/**
 * This is an example implementation of TrustStoreCallBack interface to be used in test cases.
 */
public class TrustStoreCallBackImpl implements TrustStoreCallBack {
    private static String trustStoreFilePath = "spTrustStore.jks";
    private static String trustStorePassword = "rahasnymSPTS";
    private static KeyStore trustStore = null;

    public TrustStoreCallBackImpl() throws RahasNymException {
        try {
            //read the trust store store file and load it into the trust store object.
            trustStore = KeyStore.getInstance("JKS");
            char[] storePass = trustStorePassword.toCharArray();
            InputStream trustStoreFile = getClass().getClassLoader().getResourceAsStream(trustStoreFilePath);
            trustStore.load(trustStoreFile, storePass);

        } catch (CertificateException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in loading the trust store.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in loading the trust store.");
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in loading the trust store.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in loading the trust store.");
        }
    }

    @Override
    public Certificate getCertificateFromAlias(String certAlias) throws RahasNymException {
        try {
            return trustStore.getCertificate(certAlias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in retrieving the certificate.");
        }
    }
}
