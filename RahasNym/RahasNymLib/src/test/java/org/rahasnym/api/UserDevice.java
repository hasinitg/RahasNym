package org.rahasnym.api;

import org.json.JSONException;
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
    public static void main(String[] args) throws RahasNymException, IOException, JSONException {
        //initialize IDMM Config
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        //idpAccessInfo.setUrl("http://localhost:8080/IDP/service/idp");
        idpAccessInfo.setUrl("http://128.10.25.207:8080/IDP/service/idp");
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
        for (int i = 0; i < 100; i++) {
            Long start = System.nanoTime();
            ClientAPI client = new ClientAPI();
            String policy = client.requestPolicy("http://128.10.25.206:8080/amazingshop/service/shop");
            //String policy = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            //System.out.println("SP policy: " + policy);
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setSpURL("http://128.10.25.206:8080/amazingshop/service/shop");
            //authInfo.setSpURL("http://localhost:8080/amazingshop/service/shop");
            authInfo.setPseudonym("hasi");
            String sessionId = client.authenticate(authInfo);
            Long end = System.nanoTime();
            Long time = end - start;
            commTimes.add(time);
        }
        Long totalTime = 0L;
        for (Long commTime : commTimes) {
            totalTime += commTime;
        }
        double average = totalTime/100.0;
        double avgSec = average/MILLIS_IN_NANO;
        System.out.println("Total time in milli sec warm up: " + avgSec);
        //Long av = time/1000;
        //System.out.println(time);
        //record end time
        List<Long> commTimes2 = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Long start = System.nanoTime();
            ClientAPI client = new ClientAPI();
            String policy = client.requestPolicy("http://128.10.25.206:8080/amazingshop/service/shop");
            //String policy = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            //System.out.println("SP policy: " + policy);
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setSpURL("http://128.10.25.206:8080/amazingshop/service/shop");
            //authInfo.setSpURL("http://localhost:8080/amazingshop/service/shop");
            authInfo.setPseudonym("hasi");
            String sessionId = client.authenticate(authInfo);
            Long end = System.nanoTime();
            Long time = end - start;
            commTimes2.add(time);
        }
        Long totalTime2 = 0L;
        for (Long commTime : commTimes2) {
            totalTime2 += commTime;
        }
        double average2 = totalTime2/1000.0;
        double avgSec2 = average/MILLIS_IN_NANO;
        System.out.println("Total time in milli sec: " + avgSec);
        //calculate average

    }

}
