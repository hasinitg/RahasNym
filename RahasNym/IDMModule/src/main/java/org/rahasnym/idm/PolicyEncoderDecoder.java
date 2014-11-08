package org.rahasnym.idm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/1/14
 * Time: 5:35 PM
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * This class handles reading Identity Verification Policy from file, writing to policy file
 * and converting policy object into JSON and vice versa.
 */
public class PolicyEncoderDecoder {

    public void writePolicyToFile(IDVPolicy policy) {

    }

    public IDVPolicy readPolicyFromFile(String policyFilePath) throws IDMException {
        IDVPolicy idvPolicy;
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            File file = new File(policyFilePath);
            Document policy = docBuilder.parse(file);
            //create a IDVPolicy object.
            idvPolicy = new IDVPolicy();
            //get the root element
            Element policyElement = policy.getDocumentElement();
            //read specifier name, specifier id, if present. either of them should be present.
            String specifierID = policyElement.getAttribute(IDMConstants.SPECIFIER_ID_ATTRIBUTE_NAME);
            if (specifierID != null && !specifierID.equals(IDMConstants.EMPTY_STRING)) {
                idvPolicy.setSpecifierID(specifierID);
            }
            String specifierName = policyElement.getAttribute(IDMConstants.SPECIFIER_NAME_ATTRIBUTE_NAME);
            if (specifierName != null && !specifierName.equals(IDMConstants.EMPTY_STRING)) {
                idvPolicy.setSpecifierName(specifierName);
            }
            if (specifierID.equals(IDMConstants.EMPTY_STRING) && specifierName.equals(IDMConstants.EMPTY_STRING)) {
                String error = "Either specifier id or specifier name should be mentioned.";
                throw new IDMException(error);
            }
            //read the list of rule elements
            NodeList ruleElements = policyElement.getElementsByTagName("Rule");
            for (int i = 0; i < ruleElements.getLength(); i++) {
                //for each rule element, read its sub elements and create a rule in the IDVPolicy object
                IDVPolicy.Rule rule = idvPolicy.new Rule();
                Node ruleElement = ruleElements.item(i);
                Node idAttribute = ruleElement.getAttributes().getNamedItem(IDMConstants.RULE_ID_ATTRIBUTE_NAME);
                String id = idAttribute.getTextContent();
                rule.setId(id);

                //read condition set element(s) and target element which are children of rule element.
                NodeList childrenOfRuleElement = ruleElement.getChildNodes();
                for (int j = 0; j < childrenOfRuleElement.getLength(); j++) {
                    Node currentChild = childrenOfRuleElement.item(j);
                    String childName = currentChild.getNodeName();

                    if (childName.equals(IDMConstants.CONDITION_SET_ELEMENT_NAME)) {
                        IDVPolicy.ConditionSet conditionSet = idvPolicy.new ConditionSet();
                        //read applies to attribute(s) of condition_set element.
                        Node appliesToAttribute = currentChild.getAttributes().getNamedItem(IDMConstants.APPLIES_TO_ATTRIBUTE_NAME);
                        String appliesToString = appliesToAttribute.getTextContent();
                        String[] appliesToArray = appliesToString.split(",");

                        for (String attribute : appliesToArray) {
                            conditionSet.addAppliesTo(attribute);
                        }
                        //read the conditions
                        NodeList conditions = currentChild.getChildNodes();
                        for (int k = 0; k < conditions.getLength(); k++) {
                            Node currentCondition = conditions.item(k);
                            String currentConditionName = currentCondition.getNodeName();
                            if (currentConditionName.equals(IDMConstants.DISCLOSURE_ELEMENT_NAME)) {
                                String disclosureVals = currentCondition.getTextContent();
                                String[] disclosureValArray = disclosureVals.split(",");
                                for (String disclosureVal : disclosureValArray) {
                                    conditionSet.addDisclosureVal(disclosureVal);
                                }
                            } else if (currentConditionName.equals(IDMConstants.PSEUDONYM_CARDINALITY_ELEMENT_NAME)) {
                                String cardinalityVals = currentCondition.getTextContent();
                                String[] cardinalityValArray = cardinalityVals.split(",");
                                for (String cardinalityVal : cardinalityValArray) {
                                    conditionSet.addPseudonymCardinalityVal(cardinalityVal);
                                }
                            } else if (currentConditionName.equals(IDMConstants.SUBJECT_VERIFICATION_ELEMENT_NAME)) {
                                String subjVerificationVals = currentCondition.getTextContent();
                                String[] subjVerificationValArray = subjVerificationVals.split(",");
                                for (String subjVerificationVal : subjVerificationValArray) {
                                    conditionSet.addSubjectVerificationVal(subjVerificationVal);
                                }
                            }
                        }
                        rule.addConditionSet(conditionSet);
                    } else if (childName.equals(IDMConstants.TARGET_ELEMENT_NAME)) {
                        IDVPolicy.Target target = idvPolicy.new Target();
                        Node targetChild = currentChild.getFirstChild();
                        String targetChildName = targetChild.getNodeName();
                        if (targetChildName.equals(IDMConstants.SERVICE_PROVIDERS_ELEMENT_NAME)) {
                            String serviceProviders = targetChild.getTextContent();
                            String[] serviceProvidersArray = serviceProviders.split(",");
                            for (String serviceProvider : serviceProvidersArray) {
                                target.addServiceProvider(serviceProvider);
                            }
                        } else if (targetChildName.equals(IDMConstants.OPERATIONS_ELEMENT_NAME)) {
                            String operations = targetChild.getTextContent();
                            String[] operationsArray = operations.split(",");
                            for (String operation : operationsArray) {
                                target.addOperation(operation);
                            }
                        }
                        rule.setTarget(target);
                    }
                }
                idvPolicy.addRule(rule);
            }
        } catch (ParserConfigurationException e) {
            String error = "Error in creating document builder to pass the policy file.";
            System.out.println(error);
            e.printStackTrace();
            throw new IDMException(error);
        } catch (SAXException e) {
            String error = "Error in parsing the policy file";
            System.out.println(error);
            e.printStackTrace();
            throw new IDMException(error);
        } catch (IOException e) {
            String error = "I/O exception when reading the policy file.";
            System.out.println(error);
            e.printStackTrace();
            throw new IDMException(error);
        }
        return idvPolicy;
    }

    public String toJSON(IDVPolicy policy) {
        return "";
    }

    public IDVPolicy fromPolicy(String JSONPolicy) {
        return new IDVPolicy();
    }

}
