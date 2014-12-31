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

import java.io.IOException;

/**
 * This is the entry point to the IDMM.
 */
public class IDMMRunner {
    public static void main(String[] args) throws IOException, RahasNymException, JSONException {
        //set configurations of IDMM
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        //start IDMM
        IDMMAPI idmmapi = new IDMMAPI();
        idmmapi.handleIDTRequests();
    }
}
