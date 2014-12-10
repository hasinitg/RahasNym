package org.rahasnym.api;

import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Test;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.communication.policy.IDVPolicy;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/8/14
 * Time: 6:10 AM
 */
public class TestPolicyDecoder {
    @Test
    public void testPolicyDecoder1() {
        //System.out.println(new File("").getAbsolutePath());
        String serverPolicy = "src/test/java/org/rahasnym/api/policies/serverPolicy";
        JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
        try {
            IDVPolicy policy = policyDecoder.readPolicy(serverPolicy);
            String specifierName = policy.getSpecifierName();
            Assert.assertEquals("amazon.com", specifierName);
            String specifierID = policy.getSpecifierID();
            Assert.assertEquals("c225b4e0-eb57-11e3-ac10-0800200c9a66", specifierID);
            List<IDVPolicy.Rule> rules = policy.getRules();
            Assert.assertEquals(2, rules.size());
            int i = 1;
            for (IDVPolicy.Rule rule : rules) {
                List<IDVPolicy.ConditionSet> conditionSets = rule.getConditionSets();
                IDVPolicy.Target target = rule.getTarget();
                List<String> operations = target.getOperations();
                if (i == 1) {
                    Assert.assertEquals(2, conditionSets.size());
                    Assert.assertEquals(1, operations.size());
                    int j = 1;
                    for (IDVPolicy.ConditionSet conditionSet : conditionSets) {
                        List<String> attributes = conditionSet.getAppliesTo();
                        if (j == 1) {
                            Assert.assertEquals(2, attributes.size());
                            Assert.assertEquals("email",attributes.get(0));
                            Assert.assertEquals("shipping_address", attributes.get(1));
                        } else {
                            Assert.assertEquals(1, attributes.size());
                            System.out.println(attributes.get(0));
                            Assert.assertEquals("CCN", attributes.get(0));
                        }
                        j++;
                    }
                } else {
                    Assert.assertEquals(1, conditionSets.size());
                    Assert.assertEquals(2, operations.size());
                    int j = 1;
                    for (IDVPolicy.ConditionSet conditionSet : conditionSets) {
                        List<String> attributes = conditionSet.getAppliesTo();
                        if (j == 1) {
                            Assert.assertEquals(1, attributes.size());
                            Assert.assertEquals("email", attributes.get(0));
                        }
                        j++;
                    }
                }
                i++;
            }
        } catch (IOException e) {
            Assert.fail("I/O Exception in reading the policy.");
        } catch (JSONException e) {
            Assert.fail("JSON parsing error in reading the policy.");
        } catch (RahasNymException e) {
            Assert.fail("Error in reading policy." + e.getMessage());
        }
    }

    @Test
    public void testPolicyDecoder2() {
        String clientPolicy = "src/test/java/org/rahasnym/api/policies/clientPolicy";
        JSONPolicyDecoder policyDecoder = new JSONPolicyDecoder();
        try {
            IDVPolicy policy = policyDecoder.readPolicy(clientPolicy);
            String specifierName = policy.getSpecifierName();
            Assert.assertEquals("HTG", specifierName);
            List<IDVPolicy.Rule> rules = policy.getRules();
            Assert.assertEquals(1, rules.size());
            for (IDVPolicy.Rule rule : rules) {
                List<IDVPolicy.ConditionSet> conditionSets = rule.getConditionSets();
                Assert.assertEquals(1, conditionSets.size());
                IDVPolicy.Target target = rule.getTarget();
                List<String> sps = target.getServiceProviders();
                Assert.assertEquals(1, sps.size());
                Assert.assertNotNull(target.getOverridingAlgorithm());
                for (IDVPolicy.ConditionSet conditionSet : conditionSets) {
                    List<String> attributes = conditionSet.getAppliesTo();
                    Assert.assertEquals(1, attributes.size());
                    Assert.assertEquals("email", attributes.get(0));
                }
            }
        } catch (IOException e) {
            Assert.fail("I/O Exception in reading the policy.");
        } catch (JSONException e) {
            Assert.fail("JSON parsing error in reading the policy.");
        } catch (RahasNymException e) {
            Assert.fail("Error in reading policy." + e.getMessage());
        }
    }
}
