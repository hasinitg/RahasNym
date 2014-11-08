package org.rahasnym.api;

import org.json.JSONException;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.communication.policy.DisclosureValues;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.communication.policy.PolicyCombiner;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 1:28 PM
 */
public class TestPolicyCombiner {
    public static void main(String[] args) throws JSONException, IDMException, IOException {
        /*String[] values = {Constants.PLAIN_TEXT, Constants.ZKP_I, Constants.ZKP_NI_S, Constants.ZKP_NI};

        List<Integer> vals = new ArrayList<>();
        for (String value : values) {
            vals.add((DisclosureValues.valueOf(value)).getPriority());
        }
        for (Integer val : vals) {
            System.out.println(val);
        }
        Collections.sort(vals);
        for (Integer val : vals) {
            System.out.println(val);
        }*/
        testPolicyCombiningAlgorithm();
    }

    private static void testPolicyCombiningAlgorithm() throws IOException, JSONException, IDMException {
        String clientPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/main/resources/clientPolicy";
        String serverPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/main/resources/serverPolicy";

        JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
        IDVPolicy clientPolicy = policyDecoder.readPolicy(clientPolicyPath);

        IDVPolicy serverPolicy = policyDecoder.readPolicy(serverPolicyPath);

        PolicyCombiner policyCombiner = new PolicyCombiner();
        IDVPolicy combinedPolicy = policyCombiner.getCombinedPolicy(serverPolicy, clientPolicy, "sign_up");
    }
}
