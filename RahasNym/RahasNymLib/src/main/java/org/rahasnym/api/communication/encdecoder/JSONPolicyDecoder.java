package org.rahasnym.api.communication.encdecoder;

import org.json.*;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.policy.IDVPolicy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/6/14
 * Time: 7:28 PM
 */

/**
 * This reads policy written in JSON format into a IDVPolicy object.
 */
public class JSONPolicyDecoder {

    public IDVPolicy decodePolicy(String policyString) throws IOException, JSONException, RahasNymException {
        //TODO: need to validate if the policy adheres to the expected schema.
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(policyString));
        //extract policy object
        JSONObject policyObj = (JSONObject) decodedJsonObj.opt(Constants.POLICY);

        IDVPolicy idvPolicy = new IDVPolicy();
        String specifierID = policyObj.optString(Constants.SPECIFIER_ID);
        String specifierName = policyObj.optString(Constants.SPECIFIER_NAME);
        if(specifierName == null){
            throw new RahasNymException("Error in server policy. Specifier Name is not mentioned.");
        }
        idvPolicy.setSpecifierID(specifierID);
        idvPolicy.setSpecifierName(specifierName);
        //extract rules array
        JSONArray jsonRules = policyObj.optJSONArray(Constants.RULE);
        if (jsonRules != null) {
            for (int i = 0; i < jsonRules.length(); i++) {
                IDVPolicy.Rule rule = idvPolicy.new Rule();
                JSONObject jsonRule = jsonRules.getJSONObject(i);
                rule.setId(jsonRule.optString(Constants.RULE_ID));
                //extract target of a rule
                JSONObject jsonTarget = (JSONObject) jsonRule.opt(Constants.TARGET);
                IDVPolicy.Target target = idvPolicy.new Target();
                JSONArray jsonOperations = jsonTarget.optJSONArray(Constants.OPERATIONS);
                if (jsonOperations != null) {
                    for (int p = 0; p < jsonOperations.length(); p++) {
                        target.addOperation(jsonOperations.getString(p));
                    }
                }
                JSONArray jsonSPs = jsonTarget.optJSONArray(Constants.SERVICE_PROVIDERS);
                if (jsonSPs != null) {
                    for (int s = 0; s < jsonSPs.length(); s++) {
                        target.addServiceProvider(jsonSPs.getString(s));
                    }
                }
                String overridingAlgo = jsonTarget.optString(Constants.OVERRIDING_ALGORITHM);
                target.setOverridingAlgorithm(overridingAlgo);
                rule.setTarget(target);
                //extract condition set array in a particular rule
                JSONArray jsonConditionSetArray = jsonRule.optJSONArray(Constants.CONDITION_SET);
                for (int j = 0; j < jsonConditionSetArray.length(); j++) {
                    IDVPolicy.ConditionSet conditionSet = idvPolicy.new ConditionSet();
                    JSONObject jsonConditionSet = jsonConditionSetArray.getJSONObject(j);
                    //extract values for different conditions in a condition set
                    JSONArray jsonAppliesToArray = jsonConditionSet.optJSONArray(Constants.APPLIES_TO);
                    if (jsonAppliesToArray != null) {
                        for (int k = 0; k < jsonAppliesToArray.length(); k++) {
                            conditionSet.addAppliesTo(jsonAppliesToArray.getString(k));
                        }
                    }
                    JSONArray jsonDisclosureValues = jsonConditionSet.optJSONArray(Constants.DISCLOSURE);
                    if (jsonDisclosureValues != null) {
                        for (int l = 0; l < jsonDisclosureValues.length(); l++) {
                            conditionSet.addDisclosureVal(jsonDisclosureValues.getString(l));
                        }
                    }
                    JSONArray jsonSubjVeriValues = jsonConditionSet.optJSONArray(Constants.SUBJECT_VERIFICATION);
                    if (jsonSubjVeriValues != null) {
                        for (int m = 0; m < jsonSubjVeriValues.length(); m++) {
                            conditionSet.addSubjectVerificationVal(jsonSubjVeriValues.getString(m));
                        }
                    }
                    JSONArray jsonPseudonymCardinality = jsonConditionSet.optJSONArray(Constants.PSEUDONYM_CARDINALITY);
                    if (jsonPseudonymCardinality != null) {
                        for (int n = 0; n < jsonPseudonymCardinality.length(); n++) {
                            conditionSet.addPseudonymCardinalityVal(jsonPseudonymCardinality.getString(n));
                        }
                    }
                    rule.addConditionSet(conditionSet);
                }
                idvPolicy.addRule(rule);
            }
        }
        return idvPolicy;
    }

    public String readPolicyAsString(String policyPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(policyPath));
        String policy = "", line;
        while ((line = reader.readLine()) != null) {
            policy += line;
        }
        return policy;
    }

    public IDVPolicy readPolicy(String policyPath) throws IOException, JSONException, RahasNymException {
        String policy = readPolicyAsString(policyPath);
        return decodePolicy(policy);
    }

}
