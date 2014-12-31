package org.rahasnym.serviceprovider;

import org.rahasnym.api.RandomString;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.verifierapi.VerifierAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private static Map<String, String> receiptMap = new HashMap<>();

    private SPConfig() throws IOException {
        verifierAPI = new VerifierAPI();
        spPolicyString = verifierAPI.getIDVPolicyFromClassLoader(policyPath);
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

    public String getSpPolicyString() {
        return spPolicyString;
    }

    public String issueReceipt() {
        String receipt = new RandomString().generateRandomString();
        String sessionID = UUID.randomUUID().toString();
        receiptMap.put(sessionID, receipt);
        return sessionID + "," + receipt;
    }

    public String retrieveReceipt(String sessionID){
        return receiptMap.get(sessionID);
    }
}
