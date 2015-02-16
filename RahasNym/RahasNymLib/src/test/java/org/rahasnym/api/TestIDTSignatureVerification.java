package org.rahasnym.api;

import junit.framework.Assert;
import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.json.JSONException;
import org.junit.Test;
import org.rahasnym.api.idenity.IDTRequestMessage;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityToken;
import org.rahasnym.api.idpapi.IDPConfig;
import org.rahasnym.api.idpapi.IDTTokenFactory;
import org.rahasnym.api.verifierapi.IdentityVerificationHandler;
import org.rahasnym.api.verifierapi.VerifierCallBackManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/7/15
 * Time: 12:50 PM
 */
public class TestIDTSignatureVerification {
    private static String IDPKeyStorePassword = "rahasnymIDP";
    private static String IDPKeyStorePath = "src/test/java/org/rahasnym/api/IDPkeystore.jks";
    private static String IDPKeyAlias = "rahasnymIDPCert";

    @Test
    public void testIDTSignatureVerification() {
        IDTRequestMessage idtRequestMessage = new IDTRequestMessage();
        idtRequestMessage.setAttributeName("email");
        idtRequestMessage.setSpIdentity("amazon.com");
        idtRequestMessage.setPseudonym("hasi");

        IDTTokenFactory tokenFactory = new IDTTokenFactory();
        try {
            //initialize IDP config
            configureKeyStoreInIDP();

            //create IDT with signature in it and encode IDT
            String password = "543&*^";
            byte[] salt = CryptoUtil.generateSalt(CryptoLibConstants.DEFAULT_LENGTH_OF_SALT);
            BigInteger secretBIG = CryptoUtil.getCommittableThruPBKDF(password, salt, CryptoLibConstants.SECRET_BIT_LENGTH,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);
            idtRequestMessage.setEncryptedSecret(secretBIG.toString());
            IdentityToken IDT = tokenFactory.createIdentityToken(idtRequestMessage, "hasini");
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            String encodedIDT = encoderDecoder.encodeIdentityToken(IDT);
            System.out.println("Encoded identity token: " + encodedIDT);

            String concatInfo = encoderDecoder.getConcatenatedInfoFromIDT(IDT);

            //System.out.println(concatInfo);

            //decode IDT and verify signature
            IdentityToken decodedIDT = encoderDecoder.decodeIdentityToken(encodedIDT);
            String concatInfoInDecodedIDT = encoderDecoder.getConcatenatedInfoFromIDT(decodedIDT);
            //System.out.println(concatInfoInDecodedIDT);

            //check if two concat info is equal
            Assert.assertEquals(concatInfo, concatInfoInDecodedIDT);

            //register trust store callback impl in verifier API
            TrustStoreCallBackImpl trustStoreCallBackImpl = new TrustStoreCallBackImpl();
            VerifierCallBackManager.registerTrustStoreCallBack(trustStoreCallBackImpl);

            IdentityVerificationHandler idvHandler = new IdentityVerificationHandler();
            idvHandler.verifySignatureOnIDT(decodedIDT);

        } catch (RahasNymException e) {
            Assert.fail("Error in creating identity token or verifying the signature.");
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Error in creating the secret BIG.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Error in creating the secret BIG.");
        } catch (JSONException e) {
            Assert.fail("Error in encoding the IDT.");
        } catch (ParseException e) {
            Assert.fail("Error in decoding the IDT.");
        } catch (UnrecoverableKeyException e) {
            Assert.fail("Error in configuring key store in IDP.");
        } catch (CertificateException e) {
            Assert.fail("Error in configuring key store in IDP.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Error in configuring key store in IDP.");
        } catch (KeyStoreException e) {
            Assert.fail("Error in configuring key store in IDP.");
        } catch (IOException e) {
            Assert.fail("Error in configuring key store in IDP.");
        }
    }

    private void configureKeyStoreInIDP() throws CryptoAlgorithmException, NoSuchAlgorithmException,
            KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        IDPConfig idpConfig = IDPConfig.getInstance();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        char[] storePass = IDPKeyStorePassword.toCharArray();
        FileInputStream keyStoreFile = new FileInputStream(IDPKeyStorePath);
        keyStore.load(keyStoreFile, storePass);
        idpConfig.setRSAPrivateKey((PrivateKey) keyStore.getKey(IDPKeyAlias, storePass));
        idpConfig.setCertificateAlias(IDPKeyAlias);
    }
}
