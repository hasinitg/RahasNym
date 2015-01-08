package org.rahasnym.api;

import junit.framework.Assert;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.junit.Test;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;
import org.rahasnym.api.idpapi.IDPConfig;
import org.rahasnym.api.verifierapi.VerifierCallBackManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/1/15
 * Time: 4:10 PM
 */
public class TestProtocolExecution {
    private static String IDPKeyStorePassword = "rahasnymIDP";
    private static String IDPKeyStorePath = "src/test/java/org/rahasnym/api/IDPkeystore.jks";
    private static String IDPKeyAlias = "rahasnymIDPCert";
    private static IDMMThread idmmThread = null;

    @Test
    public void testZKPIExecution() {
        try {
            //initialize IDMM Config
            IDMMConfig idmmConfig = IDMMConfig.getInstance();
            idmmConfig.setUserIDVPolicy("src/test/java/org/rahasnym/api/policies/clientPolicyZKP_I");
            idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
            IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
            idpAccessInfo.setUsername("hasini");
            //idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
            idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);

            //initialize IDMM
            if (idmmThread == null) {
                idmmThread = new IDMMThread();
                idmmThread.start();
            }

            //initialize IDP Config
            configureKeyStoreInIDP();

            //register trust store callback impl in verifier API
            TrustStoreCallBackImpl trustStoreCallBackImpl = new TrustStoreCallBackImpl();
            VerifierCallBackManager.registerTrustStoreCallBack(trustStoreCallBackImpl);

            ClientAPI client = new ClientAPI();
            JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
            String policy = policyDecoder.readPolicyAsString("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_I");

            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setPseudonym("hasi");

            String authResponse = client.authenticateINVM(authInfo);
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            String authResult = encoderDecoder.decodeAuthResult(authResponse);
            Assert.assertEquals(Constants.AUTH_SUCCESS, authResult);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testZKPNIExecution() {
        try {
            //initialize IDMM Config
            IDMMConfig idmmConfig = IDMMConfig.getInstance();
            idmmConfig.setUserIDVPolicy("src/test/java/org/rahasnym/api/policies/clientPolicyZKP_NI");
            idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
            IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
            idpAccessInfo.setUsername("hasini");
            //idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
            idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);

            //initialize IDMM
            if (idmmThread == null) {

                idmmThread = new IDMMThread();
                idmmThread.start();
            }

            //initialize IDP Config
            configureKeyStoreInIDP();

            //register trust store callback impl in verifier API
            TrustStoreCallBackImpl trustStoreCallBackImpl = new TrustStoreCallBackImpl();
            VerifierCallBackManager.registerTrustStoreCallBack(trustStoreCallBackImpl);

            ClientAPI client = new ClientAPI();
            //String policy = client.requestPolicyInVM("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_NI");
            JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
            String policy = policyDecoder.readPolicyAsString("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_NI");
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setPseudonym("hasi");

            String authResponse = client.authenticateINVM(authInfo);
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            String authResult = encoderDecoder.decodeAuthResult(authResponse);
            Assert.assertEquals(Constants.AUTH_SUCCESS, authResult);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testZKPNISExecution() {
        try {
            //initialize IDMM Config
            IDMMConfig idmmConfig = IDMMConfig.getInstance();
            idmmConfig.setUserIDVPolicy("src/test/java/org/rahasnym/api/policies/clientPolicyZKP_NI_S");
            idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
            IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
            idpAccessInfo.setUsername("hasini");
            //idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
            idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);

            //initialize IDMM
            if (idmmThread == null) {
                idmmThread = new IDMMThread();
                idmmThread.start();
            }

            //initialize IDP Config
            configureKeyStoreInIDP();

            //register trust store callback impl in verifier API
            TrustStoreCallBackImpl trustStoreCallBackImpl = new TrustStoreCallBackImpl();
            VerifierCallBackManager.registerTrustStoreCallBack(trustStoreCallBackImpl);

            ClientAPI client = new ClientAPI();
            JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
            String policy = policyDecoder.readPolicyAsString("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_NI_S");
            String receipt = new RandomString().generateRandomString();
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setPseudonym("hasi");
            authInfo.setReceipt(receipt);

            String authResponse = client.authenticateINVM(authInfo);
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            String authResult = encoderDecoder.decodeAuthResult(authResponse);
            Assert.assertEquals(Constants.AUTH_SUCCESS, authResult);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
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
