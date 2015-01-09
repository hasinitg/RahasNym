package org.rahasnym.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/19/14
 * Time: 1:53 PM
 */

/**
 * This class simulates the operations of user device in a real world deployment of
 * an identity management system supported by RahasNym.
 */
public class UserDeviceInDeployment {

    private static String IDP_URL = "http://localhost:8080/IDP/service/idp";
    //private static String IDP_URL = "http://128.10.25.207:8080/IDP/service/idp";
    private static String SP_URL = "http://localhost:8080/amazingshop/service/shop";
    //private static String SP_URL = "http://128.10.25.206:8080/amazingshop/service/shop";

    private static IDMMThread idmmThread = null;

    /*Comment/uncomment the below pairs of constants according to the protocol needs to be tested.
    * In addition to that, you also need to include the proper SP_Policy in Service Provider webapp and re-deploy it.*/
    //private static String protocol = Constants.ZKP_I;
    //private static String userIDVPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicyZKP_I";

    //private static String protocol = Constants.ZKP_NI;
    //private static String userIDVPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicyZKP_NI";

    private static String protocol = Constants.ZKP_NI_S;
    private static String userIDVPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicyZKP_NI_S";

    public static void main(String[] args) throws IOException, RahasNymException, JSONException {
        //initialize IDMM Config
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy(userIDVPolicyPath);
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        idpAccessInfo.setUrl(IDP_URL);
        idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);
        //TODO: configure IDP urls

        //initialize IDMM
        System.out.println("IDMM starting.");
        idmmThread = new IDMMThread();
        idmmThread.start();
        System.out.println("IDMM started.");


        ClientAPI client = new ClientAPI();
        String spresponse = null;
        if (protocol.equals(Constants.ZKP_NI_S)) {
            spresponse = client.requestPolicyWithReceipt(SP_URL);
        } else {
            spresponse = client.requestPolicy(SP_URL);
        }
        JSONObject spResp = new JSONObject(new JSONTokener(spresponse));
        String policy = spResp.optString(Constants.POLICY);
        String receipt = spResp.optString(Constants.TRANSACTION_RECEIPT);
        String sessionid = spResp.optString(Constants.SESSION_ID);

        //create the AuthInfo object to pass into the client API.
        AuthInfo authInfo = new AuthInfo();
        authInfo.setOperation("sign_up");
        authInfo.setPolicy(policy);
        if (receipt != null) {
            authInfo.setReceipt(receipt);
        }
        if (sessionid != null) {
            authInfo.setSessionID(sessionid);
        }
        authInfo.setSpURL(SP_URL);
        authInfo.setPseudonym("hasi");
        String authResult = client.authenticate(authInfo);
        //decode auth result
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        String result = encoderDecoder.decodeAuthResult(authResult);
        System.out.println(result);
        return;
    }

    @Override
    protected void finalize() throws Throwable {
        idmmThread.stopMe();
        super.finalize();
    }
}
