package org.rahasnym.idm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/18/14
 * Time: 1:50 PM
 */

import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.idmapi.IDMMAPI;
import org.rahasnym.api.idmapi.IDMMConfig;
import org.rahasnym.api.idmapi.IDPAccessInfo;

import java.io.IOException;

/**
 * This is the entry point to the IDMM.
 */
public class IDMMRunner {

    private static String IDP_URL = "http://localhost:8080/IDP/service/idp";

    public static void main(String[] args) throws IOException, RahasNymException, JSONException {
        //set configurations of IDMM
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        idpAccessInfo.setUrl(IDP_URL);
        idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);
        //start IDMM
        System.out.println("starting IDMM...");
        IDMMAPI idmmapi = new IDMMAPI();
        idmmapi.handleIDTRequests();
    }
}
