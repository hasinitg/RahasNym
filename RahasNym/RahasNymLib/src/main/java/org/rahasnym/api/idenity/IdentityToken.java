package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:09 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenPublicParams;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This represents the identity token object.
 */
public class IdentityToken {
    private String pseudoNym;
    private String spID;
    private String attributeName;
    private BigInteger identityCommitment;
    private String biometricIdentity;
    private String isSinglePseudonym;
    private Timestamp expirationTimeStamp;
    private Timestamp creationTimestamp;
    private PedersenPublicParams pedersenParams;
    private String signature;
    private String publicCert;

    public String getBiometricIdentity() {
        return biometricIdentity;
    }

    public void setBiometricIdentity(String biometricIdentity) {
        this.biometricIdentity = biometricIdentity;
    }

    public String getSinglePseudonym() {
        return isSinglePseudonym;
    }

    public void setSinglePseudonym(String singlePseudonym) {
        isSinglePseudonym = singlePseudonym;
    }

    public PedersenPublicParams getPedersenParams() {
        return pedersenParams;
    }

    public void setPedersenParams(PedersenPublicParams pedersenParams) {
        this.pedersenParams = pedersenParams;
    }

    public String getPublicCert() {
        return publicCert;
    }

    public void setPublicCert(String publicCert) {
        this.publicCert = publicCert;
    }

    public BigInteger getIdentityCommitment() {
        return identityCommitment;
    }

    public void setIdentityCommitment(BigInteger identityCommitment) {
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

    public Timestamp getExpirationTimeStamp() {
        return expirationTimeStamp;
    }

    public void setExpirationTimeStamp(Timestamp expirationTimeStamp) {
        this.expirationTimeStamp = expirationTimeStamp;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
