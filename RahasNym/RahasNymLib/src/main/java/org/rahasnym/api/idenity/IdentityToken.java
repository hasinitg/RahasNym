package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:09 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenPublicParams;

/**
 * This represents the identity token object.
 */
public class IdentityToken {
    private String pseudoNym;
    private String spID;
    private String attributeName;
    private String identityCommitment;
    private String expirationTimeStamp;
    private String creationTimestamp;
    private PedersenPublicParams pedersenParams;
    private String signature;
    private String publicCert;

    public String getIdentityCommitment() {
        return identityCommitment;
    }

    public void setIdentityCommitment(String identityCommitment) {
        this.identityCommitment = identityCommitment;
    }

    public String getPseudoNym() {
        return pseudoNym;
    }

    public void setPseudoNym(String pseudoNym) {
        this.pseudoNym = pseudoNym;
    }

    public String getSpID() {
        return spID;
    }

    public void setSpID(String spID) {
        this.spID = spID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getExpirationTimeStamp() {
        return expirationTimeStamp;
    }

    public void setExpirationTimeStamp(String expirationTimeStamp) {
        this.expirationTimeStamp = expirationTimeStamp;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
