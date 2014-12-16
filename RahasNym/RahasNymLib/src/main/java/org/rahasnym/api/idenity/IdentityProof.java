package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 1:02 PM
 */

import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents the identity information sent in the proof of identity in each ZKPK protocol.
 */
public class IdentityProof {
    private String proofType;
    List<BigInteger> helperCommitments = new ArrayList<>();
    private String secretPseudonym;
    private String secretSPID;
    private List<PedersenCommitmentProof> proofs = new ArrayList<>();
    private String hash;
    private Timestamp timestampAtProofCreation;
    private String encryptedPseydonymAtIDP;

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public List<BigInteger> getHelperCommitments() {
        return helperCommitments;
    }

    public void setHelperCommitments(List<BigInteger> helperCommitments) {
        this.helperCommitments = helperCommitments;
    }

    public void addHelperCommitment(BigInteger helperCommitment){
        this.helperCommitments.add(helperCommitment);
    }

    public BigInteger getHelperCommitment(){
        return this.helperCommitments.get(0);
    }

    public String getSecretPseudonym() {
        return secretPseudonym;
    }

    public void setSecretPseudonym(String secretPseudonym) {
        this.secretPseudonym = secretPseudonym;
    }

    public String getSecretSPID() {
        return secretSPID;
    }

    public void setSecretSPID(String secretSPID) {
        this.secretSPID = secretSPID;
    }

    public List<PedersenCommitmentProof> getProofs() {
        return proofs;
    }

    public void setProofs(List<PedersenCommitmentProof> proofs) {
        this.proofs = proofs;
    }

    public void addProof(PedersenCommitmentProof proof){
        proofs.add(proof);
    }

    public PedersenCommitmentProof getProof(){
        return proofs.get(0);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Timestamp getTimestampAtProofCreation() {
        return timestampAtProofCreation;
    }

    public void setTimestampAtProofCreation(Timestamp timestampAtProofCreation) {
        this.timestampAtProofCreation = timestampAtProofCreation;
    }

    public String getEncryptedPseydonymAtIDP() {
        return encryptedPseydonymAtIDP;
    }

    public void setEncryptedPseydonymAtIDP(String encryptedPseydonymAtIDP) {
        this.encryptedPseydonymAtIDP = encryptedPseydonymAtIDP;
    }
}
