package org.rahasnym.api;

import org.json.JSONException;
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
        idmmConfig.setUserIDVPolicy("src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        //TODO: configure IDP urls

        //initialize IDMM
        IDMMAPI idmmAPI = new IDMMAPI();
        idmmAPI.handleIDTRequests();

        //run over 1000 times.
        //record start time
        ClientAPI client = new ClientAPI();
        String policy = client.requestPolicy("http://localhost:8080/amazingshop/service/shop");
        String sessionId = client.authenticate(policy, "sign_up", "http://localhost:8080/amazingshop/service/shop", null);
        //record end time

        //calculate average

    }

}
