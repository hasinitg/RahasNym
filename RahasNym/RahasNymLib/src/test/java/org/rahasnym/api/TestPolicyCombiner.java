package org.rahasnym.api;

import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Test;
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
    @Test
    public void testPolicyCombiningAlgorithm() throws IOException, JSONException, IDMException {
        String clientPolicyPath = "src/test/java/org/rahasnym/api/policies/clientPolicy";
        String serverPolicyPath = "src/test/java/org/rahasnym/api/policies/serverPolicy";

        JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
        IDVPolicy clientPolicy = policyDecoder.readPolicy(clientPolicyPath);

        IDVPolicy serverPolicy = policyDecoder.readPolicy(serverPolicyPath);

        PolicyCombiner policyCombiner = new PolicyCombiner();
        IDVPolicy combinedPolicy = policyCombiner.getCombinedPolicy(serverPolicy, clientPolicy, "sign_up");
        List<IDVPolicy.Rule> rules = combinedPolicy.getRules();
        for (IDVPolicy.Rule rule : rules) {
            List<IDVPolicy.ConditionSet> conditionSets = rule.getConditionSets();
            Assert.assertEquals(1, conditionSets.size());
            for (IDVPolicy.ConditionSet conditionSet : conditionSets) {
                List<String> attributes = conditionSet.getAppliesTo();
                Assert.assertEquals(1, attributes.size());
                Assert.assertEquals("email",attributes.get(0));

                List<String> disclosure = conditionSet.getDisclosure();
                Assert.assertEquals(Constants.ZKP_NI, disclosure.get(0));

                List<String> subjVerification = conditionSet.getSubjectVerification();
                Assert.assertEquals(Constants.DEFAULT_SUBJECT_VERIFICATION_VALUE, subjVerification.get(0));

                List<String> cardinality = conditionSet.getPseudonymCardinality();
                Assert.assertEquals(Constants.SINGLE,cardinality.get(0));
            }
        }
    }
}
