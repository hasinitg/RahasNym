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
    private String secretForPseudonym;
    private String secretForSPID;
    private List<PedersenCommitmentProof> proofs = new ArrayList<>();
    private String hash;
    private Timestamp timestampAtProofCreation;
    private String encryptedPseudonymAtIDP;

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

    public void addHelperCommitment(BigInteger helperCommitment) {
        this.helperCommitments.add(helperCommitment);
    }

    public BigInteger getHelperCommitment() {
        if (helperCommitments != null && helperCommitments.size() != 0) {
            return this.helperCommitments.get(0);
        } else {
            return null;
        }
    }

    public String getSecretForPseudonym() {
        return secretForPseudonym;
    }

    public void setSecretForPseudonym(String secretForPseudonym) {
        this.secretForPseudonym = secretForPseudonym;
    }

    public String getSecretForSPID() {
        return secretForSPID;
    }

    public void setSecretForSPID(String secretForSPID) {
        this.secretForSPID = secretForSPID;
    }

    public List<PedersenCommitmentProof> getProofs() {
        return proofs;
    }

    public void setProofs(List<PedersenCommitmentProof> proofs) {
        this.proofs = proofs;
    }

    public void addProof(PedersenCommitmentProof proof) {
        proofs.add(proof);
    }

    public PedersenCommitmentProof getProof() {
        if (proofs != null && proofs.size() != 0) {
            return proofs.get(0);
        } else {
            return null;
        }
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

    public String getEncryptedPseudonymAtIDP() {
        return encryptedPseudonymAtIDP;
    }

    public void setEncryptedPseudonymAtIDP(String encryptedPseudonymAtIDP) {
        this.encryptedPseudonymAtIDP = encryptedPseudonymAtIDP;
    }
}
