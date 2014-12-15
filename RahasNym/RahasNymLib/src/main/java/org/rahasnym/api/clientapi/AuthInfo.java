package org.rahasnym.api.clientapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 1:43 PM
 */

/**
 * This encapsulates the information that needs to be provided by a SP-Client application to ClientAPI
 * in order to perform pseudonymous identity verification by the RahasNym framework.
 */
public class AuthInfo {
    private String pseudonym;
    private String operation;
    private String sessionID;
    private String policy;
    private String receipt;
    private String spURL;

    public String getSpURL() {
        return spURL;
    }

    public void setSpURL(String spURL) {
        this.spURL = spURL;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
