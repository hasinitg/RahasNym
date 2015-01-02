package org.rahasnym.api;

import junit.framework.Assert;
import org.junit.Test;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/1/15
 * Time: 4:10 PM
 */
public class TestProtocolExecution {

    @Test
    public void testZKPIExecution() {
        IDMMThread idmmThread = null;
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
            idmmThread = new IDMMThread();
            idmmThread.start();

            ClientAPI client = new ClientAPI();
             /*String policy = client.requestPolicyInVM("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_I");*/
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
        } finally {
            idmmThread.stopMe();
        }
    }

    @Test
    public void testZKPNIExecution() {
        IDMMThread idmmThread = null;
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
            idmmThread = new IDMMThread();
            idmmThread.start();

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
        } finally {
            idmmThread.stopMe();
        }
    }

    /*@Test
    public void testZKPNISExecution() {
        IDMMThread idmmThread = null;
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
            idmmThread = new IDMMThread();
            idmmThread.start();

            ClientAPI client = new ClientAPI();
            //String policy = client.requestPolicyInVM("src/test/java/org/rahasnym/api/policies/serverPolicyZKP_NI_S");
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
        } finally {
            idmmThread.stopMe();
        }
    }*/


}
