package org.rahasnym.api.verifierapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/15/14
 * Time: 8:05 PM
 */

import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

import java.math.BigInteger;

/**
 * This represents the information kept in in-memory until the process of identity proof is completed.
 */
public class ProofInfo {
    private String sessionID;
    private IdentityToken identityToken;
    private IdentityProof identityProof;
    private BigInteger challenge;
    //private BigInteger sValue;

    public IdentityProof getIdentityProof() {
        return identityProof;
    }

    public void setIdentityProof(IdentityProof identityProof) {
        this.identityProof = identityProof;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public IdentityToken getIdentityToken() {
        return identityToken;
    }

    public void setIdentityToken(IdentityToken identityToken) {
        this.identityToken = identityToken;
    }

    public BigInteger getChallengeValue() {
        return challenge;
    }

    public void setChallengeValue(BigInteger challenge) {
        this.challenge = challenge;
    }

    /*public BigInteger getsValue() {
        return sValue;
    }

    public void setsValue(BigInteger sValue) {
        this.sValue = sValue;
    }*/

}