package org.rahasnym.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.idmapi.IDMMAPI;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 3:39 PM
 */
public class UserDevice {
    private static double MILLIS_IN_NANO = 1000000.0;
    private static int WARM_UP = 100;
    private static int TESTS = 1000;
    private static String IDP_URL = "http://localhost:8080/IDP/service/idp";
    //private static String IDP_URL = "http://128.10.25.207:8080/IDP/service/idp";
    private static String SP_URL = "http://localhost:8080/amazingshop/service/shop";
    //private static String SP_URL = "http://128.10.25.206:8080/amazingshop/service/shop";

    public static void main(String[] args) throws RahasNymException, IOException, JSONException {
        //initialize IDMM Config
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        //idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
        idpAccessInfo.setUrl(IDP_URL);
        idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);
        //TODO: configure IDP urls

        //initialize IDMM
        System.out.println("IDMM starting.");
        IDMMThread idmmThread = new IDMMThread();
        idmmThread.start();
        System.out.println("IDMM started.");
        //run over 1000 times.

        //record start time
        List<Long> commTimes = new ArrayList<>();
        for (int i = 0; i < WARM_UP; i++) {
            Long start = System.nanoTime();
            ClientAPI client = new ClientAPI();
            String spresponse = client.requestPolicyWithReceipt(SP_URL);
            System.out.println(spresponse);
            //String spresponse = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            JSONObject spResp = new JSONObject(new JSONTokener(spresponse));
            String policy = spResp.optString(Constants.POLICY);
            String receipt = spResp.optString(Constants.TRANSACTION_RECEIPT);
            String sessionid = spResp.optString(Constants.SESSION_ID);

            //System.out.println("SP policy: " + policy);
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setSpURL(SP_URL);
            //authInfo.setSpURL("http://localhost:8080/amazingshop/service/shop");
            authInfo.setPseudonym("hasi");

            if (receipt != null) {
                authInfo.setReceipt(receipt);
            }
            if (authInfo != null) {
                authInfo.setSessionID(sessionid);
            }

            String sessionId = client.authenticate(authInfo);
            Long end = System.nanoTime();
            Long time = end - start;
            commTimes.add(time);
        }
        Long totalTime = 0L;
        for (Long commTime : commTimes) {
            totalTime += commTime;
        }
        double average = totalTime/WARM_UP;
        double avgSec = average/MILLIS_IN_NANO;
        System.out.println("Total time in milli sec warm up: " + avgSec);

        List<Long> commTimes2 = new ArrayList<>();
        for (int i = 0; i < TESTS; i++) {
            Long start = System.nanoTime();

            ClientAPI client = new ClientAPI();
            String spresponse = client.requestPolicyWithReceipt(SP_URL);

            //String spresponse = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            JSONObject spResp = new JSONObject(new JSONTokener(spresponse));
            String policy = spResp.optString(Constants.POLICY);
            String receipt = spResp.optString(Constants.TRANSACTION_RECEIPT);
            String sessionid = spResp.optString(Constants.SESSION_ID);

            //String policy = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            //System.out.println("SP policy: " + policy);
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            //authInfo.setSpURL("http://128.10.25.206:8080/amazingshop/service/shop");
            authInfo.setSpURL(SP_URL);
            authInfo.setPseudonym("hasi");

            if (receipt != null) {
                authInfo.setReceipt(receipt);
            }
            if (authInfo != null) {
                authInfo.setSessionID(sessionid);
            }

            String sessionId = client.authenticate(authInfo);
            Long end = System.nanoTime();
            Long time = end - start;
            commTimes2.add(time);
        }
        Long totalTime2 = 0L;
        for (Long commTime : commTimes2) {
            totalTime2 += commTime;
        }
        double average2 = totalTime2/TESTS;
        double avgSec2 = average2/MILLIS_IN_NANO;
        System.out.println("Total time in milli sec: " + avgSec2);
        //calculate average

    }

}
