package org.rahasnym.api;

import org.json.JSONException;
import org.rahasnym.api.clientapi.AuthInfo;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.idmapi.IDMMAPI;
import org.rahasnym.api.idmapi.IDMMConfig;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 3:39 PM
 */
public class UserDevice {

    public static void main(String[] args) throws RahasNymException, IOException, JSONException {
        //initialize IDMM Config
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        //TODO: configure IDP urls

        //initialize IDMM
        System.out.println("IDMM starting.");
        IDMMThread idmmThread = new IDMMThread();
        idmmThread.start();
        System.out.println("IDMM started.");
        //run over 1000 times.

        //record start time
        Long start = System.nanoTime();
        for(int i = 0; i<1000; i++){
            ClientAPI client = new ClientAPI();
            String policy = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
            System.out.println("SP policy: " + policy);
            //create the AuthInfo object to pass into the client API.
            AuthInfo authInfo = new AuthInfo();
            authInfo.setOperation("sign_up");
            authInfo.setPolicy(policy);
            authInfo.setSpURL("http://localhost:8080/amazingshop/service/shop");
            authInfo.setPseudonym("hasi");
            String sessionId = client.authenticate(authInfo);
        }
        Long end = System.nanoTime();
        Long time = end - start;
        Long av = time/1000;
        System.out.println(av);
        //record end time

        //calculate average

    }

}
