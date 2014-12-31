package org.rahasnym.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/19/14
 * Time: 1:53 PM
 */
public class UserDeviceLocal {
    public static void main(String[] args) throws IOException, RahasNymException, JSONException {
        //initialize IDMM Config
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
        //idpAccessInfo.setUrl("http://128.10.25.207:8080/IDP/service/idp");
        idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);
        //TODO: configure IDP urls

        //initialize IDMM
        System.out.println("IDMM starting.");
        IDMMThread idmmThread = new IDMMThread();
        idmmThread.start();
        System.out.println("IDMM started.");


        ClientAPI client = new ClientAPI();
        //String policy = client.requestPolicy("http://128.10.25.206:8080/amazingshop/service/shop");
        //String spresponse = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
        String spresponse = client.requestPolicyWithReceipt("http://localhost:8080/amazingshop/service/shop");
        JSONObject spResp = new JSONObject(new JSONTokener(spresponse));
        String policy = spResp.optString(Constants.POLICY);
        String receipt = spResp.optString(Constants.TRANSACTION_RECEIPT);
        String sessionid = spResp.optString(Constants.SESSION_ID);

        //System.out.println("SP policy: " + policy);
        //create the AuthInfo object to pass into the client API.
        AuthInfo authInfo = new AuthInfo();
        authInfo.setOperation("sign_up");
        authInfo.setPolicy(policy);
        if (receipt != null) {
            authInfo.setReceipt(receipt);
        }
        if (authInfo != null) {
            authInfo.setSessionID(sessionid);
        }
        //authInfo.setSpURL("http://128.10.25.206:8080/amazingshop/service/shop");
        authInfo.setSpURL("http://localhost:8080/amazingshop/service/shop");
        authInfo.setPseudonym("hasi");
        String sessionId = client.authenticate(authInfo);
    }
}
