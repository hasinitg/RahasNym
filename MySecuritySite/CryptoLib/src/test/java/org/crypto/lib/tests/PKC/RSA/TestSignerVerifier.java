package org.crypto.lib.tests.PKC.RSA;

import junit.framework.Assert;
import org.crypto.lib.PKC.RSA.SignerVerifier;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/7/15
 * Time: 11:12 AM
 */

public class TestSignerVerifier {
    @Test
    public void testMessageSigningVerification() {
        String keyStorePath = "src/test/java/org/crypto/lib/tests/PKC/RSA/IDPkeystore.jks";
        String trustStorePath = "src/test/java/org/crypto/lib/tests/PKC/RSA/spTrustStore.jks";
        String keyStorePassword = "rahasnymIDP";
        String IDPKeyAlias = "rahasnymIDPCert";
        String trustStorePassword = "rahasnymSPTS";
        String plainText = "Plain text to be signed.";

        try {
            //load private key and sign
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] keystorePass = keyStorePassword.toCharArray();
            FileInputStream fileInputStream = new FileInputStream(keyStorePath);
            keyStore.load(fileInputStream, keystorePass);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(IDPKeyAlias, keystorePass);
            SignerVerifier signer = new SignerVerifier();
            String signature = signer.signMessageAsString(plainText, privateKey);

            //load public key from trust store and verify the signature
            KeyStore trustStore = KeyStore.getInstance("JKS");
            char[] trustStorePass = trustStorePassword.toCharArray();
            FileInputStream fs = new FileInputStream(trustStorePath);
            trustStore.load(fs, trustStorePass);
            Certificate publicCert = trustStore.getCertificate(IDPKeyAlias);
            SignerVerifier verifier = new SignerVerifier();
            boolean verified = verifier.verifySignature(plainText, signature, publicCert);
            Assert.assertEquals(true, verified);
        } catch (KeyStoreException e) {
            Assert.fail("Error in loading the keystore.");
        } catch (FileNotFoundException e) {
            Assert.fail("Error in loading the keystore.");
        } catch (CertificateException e) {
            Assert.fail("Error in loading the keystore.");
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Error in loading the keystore.");
        } catch (IOException e) {
            Assert.fail("Error in loading the keystore.");
        } catch (UnrecoverableKeyException e) {
            Assert.fail("Error in retrieving the private key");
        } catch (SignatureException e) {
            Assert.fail("Error in signing.");
        } catch (InvalidKeyException e) {
            Assert.fail("Error in signing.");
        }
    }
}
