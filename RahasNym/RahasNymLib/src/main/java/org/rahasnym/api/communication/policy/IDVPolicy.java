package org.rahasnym.api.communication.policy;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/29/14
 * Time: 11:18 AM
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This represents the Identity Verification Policy.
 */
public class IDVPolicy {

    private String specifierName;
    private String specifierID;
    private List<Rule> rules = new ArrayList<>();

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public void addRule(Rule rule){
        rules.add(rule);
    }

    public String getSpecifierName() {
        return specifierName;
    }

    public void setSpecifierName(String specifierName) {
        this.specifierName = specifierName;
    }

    public String getSpecifierID() {
        return specifierID;
    }

    public void setSpecifierID(String specifierID) {
        this.specifierID = specifierID;
    }

    public class Rule{
        private String id;
        private Target target = new Target();
        private List<ConditionSet> conditionSets = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<ConditionSet> getConditionSets() {
            return conditionSets;
        }

        public void setConditionSets(List<ConditionSet> conditionSets) {
            this.conditionSets = conditionSets;
        }

        public void addConditionSet(ConditionSet conditionSet){
            conditionSets.add(conditionSet);
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }
    }

    public class Target{
        private List<String> operations = new ArrayList<>();
        private List<String> serviceProviders = new ArrayList<>();
        private String overridingAlgorithm;

        public String getOverridingAlgorithm() {
            return overridingAlgorithm;
        }

        public void setOverridingAlgorithm(String overridingAlgorithm) {
            this.overridingAlgorithm = overridingAlgorithm;
        }

        public List<String> getOperations() {
            return operations;
        }

        public void setOperations(List<String> operations) {
            this.operations = operations;
        }

        public void addOperation(String operation){
            operations.add(operation);
        }

        public void addServiceProvider(String serviceProvider){
            serviceProviders.add(serviceProvider);
        }

        public List<String> getServiceProviders() {
            return serviceProviders;
        }

        public void setServiceProviders(List<String> serviceProviders) {
            this.serviceProviders = serviceProviders;
        }
    }

    public class ConditionSet{
        /*elements in the condition set can take multiple values. Hence defined as lists*/
        private List<String> appliesTo = new ArrayList<>();
        private List<String> disclosure = new ArrayList<>();
        private List<String> subjectVerification = new ArrayList<>();
        private List<String> pseudonymCardinality = new ArrayList<>();

        public List<String> getAppliesTo() {
            return appliesTo;
        }

        public void setAppliesTo(List<String> appliesTo) {
            this.appliesTo = appliesTo;
        }

        public void addAppliesTo(String appliesToVal){
            appliesTo.add(appliesToVal);
        }

        public List<String> getDisclosure() {
            return disclosure;
        }

        public void setDisclosure(List<String> disclosure) {
            this.disclosure = disclosure;
        }

        public void addDisclosureVal(String disclosureVal){
            disclosure.add(disclosureVal);
        }

        public List<String> getSubjectVerification() {
            return subjectVerification;
        }

        public void setSubjectVerification(List<String> subjectVerification) {
            this.subjectVerification = subjectVerification;
        }

        public void addSubjectVerificationVal(String subjectVerificationVal){
            subjectVerification.add(subjectVerificationVal);
        }

        public List<String> getPseudonymCardinality() {
            return pseudonymCardinality;
        }

        public void setPseudonymCardinality(List<String> pseudonymCardinality) {
            this.pseudonymCardinality = pseudonymCardinality;
        }

        public void addPseudonymCardinalityVal(String pseudonymCardinalityVal){
            pseudonymCardinality.add(pseudonymCardinalityVal);
        }
    }

}
