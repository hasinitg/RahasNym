package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/15/14
 * Time: 8:05 PM
 */

import java.math.BigInteger;

/**
 * This represents the information kept in in-memory until the process of identity proof is completed.
 */
public class ProofInfo {
    private String sessionID;
    private BigInteger identityBIG;
    private BigInteger secretBIG;
    private BigInteger yValue;
    private BigInteger sValue;

    public BigInteger getSecretBIG() {
        return secretBIG;
    }

    public void setSecretBIG(BigInteger secretBIG) {
        this.secretBIG = secretBIG;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public BigInteger getIdentityBIG() {
        return identityBIG;
    }

    public void setIdentityBIG(BigInteger identityBIG) {
        this.identityBIG = identityBIG;
    }

    public BigInteger getyValue() {
        return yValue;
    }

    public void setyValue(BigInteger yValue) {
        this.yValue = yValue;
    }

    public BigInteger getsValue() {
        return sValue;
    }

    public void setsValue(BigInteger sValue) {
        this.sValue = sValue;
    }

}
