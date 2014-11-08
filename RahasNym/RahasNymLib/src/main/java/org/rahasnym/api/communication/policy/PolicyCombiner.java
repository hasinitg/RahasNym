package org.rahasnym.api.communication.policy;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/29/14
 * Time: 8:06 AM
 */

import org.rahasnym.api.Constants;
import org.rahasnym.api.IDMException;

import java.util.*;

/**
 * Upon receiving the policy from server, this combines it with client's policy and return the
 * combined policy for IDMModule to obtain a matching token. Also, if there is a conflict in policy combining,
 * this notifies the user through the notification interface.
 */
public class PolicyCombiner {

    /**
     * This combines the server policy and client policy and returns the combined policy.
     * In policy combining, this looks for the rule specified by the verifier for the current operation.
     * It is **assumed** that the verifier specifies only one rule for a particular operation.
     * It is also **assumed** that verifier specifies only one condition set for a particular attribute within a given rule.
     * Then it looks for a rule in user's policy targeted at this particular verifier or at all specifiers in general.
     * If there is a rule specified for this particular verifier, then it is picked,
     * Otherwise,the rule specified in general (it is **assumed** that only one general rule exists), is picked.
     * If neither such rules found, error is shown and prompted to specify the rule.
     * If a rule is found, 'appliesTo' attributes are matched and then policy elements are matched.
     * If a non-match found, look for the policy overriding algorithm and proceed accordingly.
     *
     * @param verifierPolicy
     * @param clientPolicy
     * @param currentOperation
     * @return
     */
    public IDVPolicy getCombinedPolicy(IDVPolicy verifierPolicy, IDVPolicy clientPolicy, String currentOperation)
            throws IDMException {
        IDVPolicy combinedPolicy = new IDVPolicy();

        String verifierName = verifierPolicy.getSpecifierName();
        String overridingAlgorithm = null;
        //get the verifier rule applies to the current operation.
        List<IDVPolicy.Rule> verifierRules = verifierPolicy.getRules();
        IDVPolicy.Rule matchingVerifierRule = null;
        for (IDVPolicy.Rule verifierRule : verifierRules) {
            List<String> operations = verifierRule.getTarget().getOperations();
            if (operations.contains(currentOperation)) {
                matchingVerifierRule = verifierRule;
                break;
            }
        }
        if (matchingVerifierRule == null) {
            String error = "Error in verifier policy. No matching rule found.";
            throw new IDMException(error);
        }

        //get the client's rule applies to the particular verifier or to all verifiers in general.
        IDVPolicy.Rule matchingClientRule = null;
        //List<IDVPolicy.Rule> candidateClientRules = new ArrayList<IDVPolicy.Rule>();
        List<IDVPolicy.Rule> allClientRules = clientPolicy.getRules();
        for (IDVPolicy.Rule clientRule : allClientRules) {
            List<String> verifiers = clientRule.getTarget().getServiceProviders();
            if ((verifiers.contains(verifierName)) || (verifiers.contains(Constants.ALL))) {
                matchingClientRule = clientRule;
                overridingAlgorithm = clientRule.getTarget().getOverridingAlgorithm();
                break;
            }
        }

        Map<String, IDVPolicy.ConditionSet> verifierAttributesConditionsMap = new HashMap<>();
        /*get verifier required attributes and corresponding conditions set in order to match
        the conditions in client's rule(s).*/
        List<IDVPolicy.ConditionSet> verifierConditionSets = matchingVerifierRule.getConditionSets();
        for (IDVPolicy.ConditionSet verifierConditionSet : verifierConditionSets) {
            List<String> attributeNames = verifierConditionSet.getAppliesTo();
            for (String attributeName : attributeNames) {
                verifierAttributesConditionsMap.put(attributeName, verifierConditionSet);
            }
        }
        Set<String> attributesToBeMatched = verifierAttributesConditionsMap.keySet();
        Map<String, IDVPolicy.ConditionSet> clientAttributesConditionsMap = new HashMap<>();

        //if no matching rule, show error and prompt the user to create a rule
        if (matchingClientRule == null) {
            //todo:show error dialogue
            //todo:in all the prompts to add a new rule, show verifier rule and client's rule/policy side by side.
            //if the client adds a non-matching thing, warn then and there, so that user will not get multiple prompts.
            //also, ask to add overriding algo, for later usage.
            //todo: if the client adds a rule, populate the above client map.

        } else {
            //get the conditions specified by client for the attributes required by the verifier.
            //if (matchingClientRule != null) {
            List<IDVPolicy.ConditionSet> clientConditionSets = matchingClientRule.getConditionSets();
            for (IDVPolicy.ConditionSet clientConditionSet : clientConditionSets) {
                List<String> clientAttributes = clientConditionSet.getAppliesTo();
                for (String clientAttribute : clientAttributes) {
                    if (attributesToBeMatched.contains(clientAttribute)) {
                        clientAttributesConditionsMap.put(clientAttribute, clientConditionSet);
                        attributesToBeMatched.remove(clientAttribute);
                    }
                }
            }
            //todo: if matching conditions were not found for all the required attributes, show error.
            if (attributesToBeMatched.size() != 0) {
                //todo: show error and prompt to add *conditions* for attributes to be matched n this rule.
                //todo: if the client adds conditions, populate the above two maps and the set.
            }

            //}
            /*else {
                //look in the rules specified in general.
                for (IDVPolicy.Rule candidateClientRule : candidateClientRules) {
                    List<IDVPolicy.ConditionSet> clientConditionSets = matchingClientRule.getConditionSets();
                    for (IDVPolicy.ConditionSet clientConditionSet : clientConditionSets) {
                        List<String> clientAttributes = clientConditionSet.getAppliesTo();
                        for (String clientAttribute : clientAttributes) {
                            if (attributesToBeMatched.contains(clientAttribute)) {
                                clientAttributesConditionsMap.put(clientAttribute, clientConditionSet);
                                attributesToBeMatched.remove(clientAttribute);
                            }
                        }
                    }
                }
                if (attributesToBeMatched.size() != 0) {
                    //todo: show error and prompt to add a new rule for this verifier, with existing matching conditions
                    //and new conditions for the missing attributes.
                    //todo: if the client adds a rule, populate the above two maps and the set.
                }
            }*/
        }
        /*if code reaches this point, we should have attributes-conditions maps of both verifier and client for the
        attributes required by the verifier. Now it is time to match the conditions for each attribute and create the
        identity verification rule for the combined policy.*/
        IDVPolicy.Rule combinedRule = combinedPolicy.new Rule();

        //create a ConditionSet list to record successfully matched ConditionSet for each attribute
        List<IDVPolicy.ConditionSet> matchedConditionSets = new ArrayList<>();

        //create a map to record conflicting policy values for each attribute.
        Map<String, List<String>> conflictingAttributePolicyValuesMap = new HashMap<>();

        for (Map.Entry<String, IDVPolicy.ConditionSet> conditionSetEntry : verifierAttributesConditionsMap.entrySet()) {
            String verifierReqAttribute = conditionSetEntry.getKey();

            IDVPolicy.ConditionSet attributeConditionSet = combinedPolicy.new ConditionSet();
            attributeConditionSet.addAppliesTo(verifierReqAttribute);

            IDVPolicy.ConditionSet verifierConditions = conditionSetEntry.getValue();
            IDVPolicy.ConditionSet clientConditions = clientAttributesConditionsMap.get(verifierReqAttribute);

            List<PolicyValues> verifierEnumVals = new ArrayList<>();
            List<PolicyValues> clientEnumVals = new ArrayList<>();

            //create a list of conflicting policy values for this attribute
            List<String> conflictingPolicyValues = new ArrayList<>();
            //match disclosure values
            String matchingPolicyVal = null;
            List<String> verifierPolicyValues = verifierConditions.getDisclosure();
            List<String> clientPolicyValues = clientConditions.getDisclosure();
            //TODO: if any or both are not specified, assign the default value
            for (String verifierDisclosureValue : verifierPolicyValues) {
                verifierEnumVals.add(DisclosureValues.valueOf(verifierDisclosureValue));
                Collections.sort(verifierEnumVals, new PolicyValuesComparator());
            }
            for (String clientDisclosureValue : clientPolicyValues) {
                clientEnumVals.add(DisclosureValues.valueOf(clientDisclosureValue));
                Collections.sort(clientEnumVals, new PolicyValuesComparator());
            }

            for (PolicyValues clientVal : clientEnumVals) {
                if (verifierEnumVals.contains(clientVal)) {
                    matchingPolicyVal = clientVal.toString();
                    //add the matching value to the condition set
                    attributeConditionSet.addDisclosureVal(matchingPolicyVal);
                    break;
                }
            }
            //if matching**Val is still null and overridingAlgo is not SP_OVERRIDES, no matching value found.
            // TODO: show error and prompt to change.
            if (matchingPolicyVal == null) {
                if (Constants.SP_OVERRIDES.equals(overridingAlgorithm)) {
                    matchingPolicyVal = verifierEnumVals.get(0).toString();
                    attributeConditionSet.addDisclosureVal(matchingPolicyVal);
                } else {
                    conflictingPolicyValues.add(Constants.DISCLOSURE);
                }
            }

            //match subject verification values
            verifierEnumVals = new ArrayList<>();
            clientEnumVals = new ArrayList<>();
            matchingPolicyVal = null;
            verifierPolicyValues = verifierConditions.getSubjectVerification();
            clientPolicyValues = clientConditions.getSubjectVerification();
            //TODO:if not specified, assign default
            for (String verifierPolicyValue : verifierPolicyValues) {
                verifierEnumVals.add(SubjectVerificationValues.valueOf(verifierPolicyValue));
                Collections.sort(verifierEnumVals, new PolicyValuesComparator());
            }
            for (String clientPolicyValue : clientPolicyValues) {
                clientEnumVals.add(SubjectVerificationValues.valueOf(clientPolicyValue));
                Collections.sort(clientEnumVals, new PolicyValuesComparator());
            }

            for (PolicyValues clientVal : clientEnumVals) {
                if (verifierEnumVals.contains(clientVal)) {
                    matchingPolicyVal = clientVal.toString();
                    //add the matching value to the condition set
                    attributeConditionSet.addSubjectVerificationVal(matchingPolicyVal);
                    break;
                }
            }
            //if matching**Val is still null, no matching value found. TODO: show error and prompt to change.
            if (matchingPolicyVal == null) {
                if (Constants.SP_OVERRIDES.equals(overridingAlgorithm)) {
                    matchingPolicyVal = verifierEnumVals.get(0).toString();
                    attributeConditionSet.addSubjectVerificationVal(matchingPolicyVal);
                } else {
                    conflictingPolicyValues.add(Constants.SUBJECT_VERIFICATION);
                }
            }

            //match pseudonym cardinality values
            verifierEnumVals = new ArrayList<>();
            clientEnumVals = new ArrayList<>();
            matchingPolicyVal = null;
            verifierPolicyValues = verifierConditions.getPseudonymCardinality();
            clientPolicyValues = clientConditions.getPseudonymCardinality();
            //TODO: if not specified, use default
            for (String verifierPolicyValue : verifierPolicyValues) {
                verifierEnumVals.add(PseudonymCardinalityValues.valueOf(verifierPolicyValue));
                Collections.sort(verifierEnumVals, new PolicyValuesComparator());
            }
            for (String clientPolicyValue : clientPolicyValues) {
                clientEnumVals.add(PseudonymCardinalityValues.valueOf(clientPolicyValue));
                Collections.sort(clientEnumVals, new PolicyValuesComparator());
            }

            for (PolicyValues clientVal : clientEnumVals) {
                if (verifierEnumVals.contains(clientVal)) {
                    matchingPolicyVal = clientVal.toString();
                    //add the matching value to the condition set
                    attributeConditionSet.addPseudonymCardinalityVal(matchingPolicyVal);
                    break;
                }
            }
            //if matching**Val is still null, no matching value found. TODO: show error and prompt to change.
            if (matchingPolicyVal == null) {
                if (Constants.SP_OVERRIDES.equals(overridingAlgorithm)) {
                    matchingPolicyVal = verifierEnumVals.get(0).toString();
                    attributeConditionSet.addPseudonymCardinalityVal(matchingPolicyVal);
                } else {
                    conflictingPolicyValues.add(Constants.PSEUDONYM_CARDINALITY);
                }
            }
            if (conflictingPolicyValues.size() == 0) {
                //add the condition set to the matched condition set.
                matchedConditionSets.add(attributeConditionSet);
            } else {
                //add the attribute and conflicting policy element names to the map
                conflictingAttributePolicyValuesMap.put(verifierReqAttribute, conflictingPolicyValues);
            }
        }
        if (conflictingAttributePolicyValuesMap.size() == 0) {
            combinedRule.setConditionSets(matchedConditionSets);
        } else {
            //TODO:prompt the user to resolve conflicts or abort the operation.
        }
        combinedPolicy.addRule(combinedRule);

        return combinedPolicy;
    }
}
