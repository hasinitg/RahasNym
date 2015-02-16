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
import org.rahasnym.api.idenity.AttributeCallBack;
import org.rahasnym.api.idenity.AttributeCallBackManager;
import org.rahasnym.api.idmapi.*;

import java.io.IOException;

/**
 * This is the entry point to the IDMM.
 */
public class IDMMRunner {

    private static String IDP_URL = "http://localhost:8080/IDP/service/idp";
    private static String PW_CONFIG_FILE = "/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/users";
    private static String ATTRIBUTE_STORE_FILE = "/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/attributeStore";

    public static void main(String[] args) throws IOException, RahasNymException, JSONException {
        //set configurations of IDMM
        IDMMConfig idmmConfig = IDMMConfig.getInstance();
        idmmConfig.setUserIDVPolicy("/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/clientPolicy");
        idmmConfig.setIDMMPort(Constants.IDM_MODULE_PORT);
        IDPAccessInfo idpAccessInfo = new IDPAccessInfo();
        idpAccessInfo.setUsername("hasini");
        idpAccessInfo.setUrl(IDP_URL);
        idmmConfig.addIDP(Constants.EMAIL_ATTRIBUTE, idpAccessInfo);
        idmmConfig.addIDP(Constants.STUDENT_ID_ATTRIBUTE, idpAccessInfo);

        //register password call back handler to supply the username/password
        //for the purpose of the prototype, this is read from the configuration file.
        PasswordCallBack passwordCallBack = new BasicPasswordCallbackHandler();
        ((BasicPasswordCallbackHandler) passwordCallBack).setConfigurationFile(PW_CONFIG_FILE);

        PasswordCallBackManager.registerPasswordCallBack(passwordCallBack);

        //register attribute callback handler for supplying the user attributes.
        //for the purpose of the prototype, it is read from the config file.
        AttributeCallBack attributeCallBack = new BasicAttributeHandler();
        ((BasicAttributeHandler)attributeCallBack).setAttributeStoreFile(ATTRIBUTE_STORE_FILE);
        AttributeCallBackManager.registerAttributeCallBack(attributeCallBack);

        //start IDMM
        System.out.println("starting IDMM...");
        IDMMAPI idmmapi = new IDMMAPI();
        idmmapi.handleIDTRequests();
    }
}
