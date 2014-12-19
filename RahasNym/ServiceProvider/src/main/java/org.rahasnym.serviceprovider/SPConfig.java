package org.rahasnym.serviceprovider;

import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.verifierapi.VerifierAPI;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/18/14
 * Time: 2:21 PM
 */
public class SPConfig {
    private static volatile SPConfig spConfig;
    private static IDVPolicy spPolicy;
    private static String spPolicyString;
    private static VerifierAPI verifierAPI;
    private String policyPath = "serverPolicy";

    private SPConfig() throws IOException {
        verifierAPI = new VerifierAPI();
        spPolicyString =  verifierAPI.getIDVPolicyFromClassLoader(policyPath);
    }

    public static SPConfig getInstance() throws IOException {
        if (spConfig == null) {
            synchronized (SPConfig.class) {
                if (spConfig == null) {
                    spConfig = new SPConfig();
                    return spConfig;
                }
            }
        }
        return spConfig;
    }

    public String getSpPolicyString(){
        return spPolicyString;
    }

}
