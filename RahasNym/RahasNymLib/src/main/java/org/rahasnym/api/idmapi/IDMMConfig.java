package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 11:13 AM
 */

import org.json.JSONException;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.communication.policy.IDVPolicy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents the configuration file of IDMM which includes the config. info required for
 * an IDMM running in a user's device. This needs to be initialized by reading the config file.
 * For the moment, we make it a singleton class for the purpose of prototype.
 */
public class IDMMConfig {

    private static int IDMMPort;
    private static IDVPolicy userIDVPolicy;
    private static IDMMConfig idmmConfig;
    private static Map<String, IDPAccessInfo> IDPUrls = new HashMap<>();

    public static IDMMConfig getInstance() {
        if (idmmConfig == null) {
            idmmConfig = new IDMMConfig();
        }
        return idmmConfig;
    }

    public IDVPolicy getUserIDVPolicy() {
        return userIDVPolicy;
    }

    public void setUserIDVPolicy(String clientPolicyPath) throws JSONException, RahasNymException, IOException {
        userIDVPolicy = new JSONPolicyDecoder().readPolicy(clientPolicyPath);
    }

    public int getIDMMPort() {
        return IDMMPort;
    }

    public void setIDMMPort(int IDMMPort) {
        IDMMConfig.IDMMPort = IDMMPort;
    }

    public void addIDP(String attributeName, IDPAccessInfo idpAccessInfo){
        IDPUrls.put(attributeName, idpAccessInfo);
    }

    public IDPAccessInfo getIDPAccessInfo(String attributeName){
        return IDPUrls.get(attributeName);
    }
}
