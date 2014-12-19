package org.rahasnym.api.idmapi;

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:58 PM
 */
public class IDVProofCreator {

    /*Need to maintain state in the case of ZKP_I*/
    private BigInteger yValue;
    private BigInteger sValue;
    private PedersenPublicParams pedersenPublicParams;
    private ZKPPedersenCommitment ZKPK;
    private PedersenCommitment helperCommitment;

    public IdentityProof createProof(IdentityToken identityToken, IDVPolicy combinedPolicy, BigInteger identityBIG, BigInteger secretBIG)
            throws JSONException, CryptoAlgorithmException, NoSuchAlgorithmException {
        //look at the policy and create the appropriate proof
        IDVPolicy.Rule rule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet conditionSet = rule.getConditionSets().get(0);
        String disclosure = conditionSet.getDisclosure().get(0);
        if (disclosure.equals(Constants.ZKP_I)) {
            return createInitialProofForZKPI(identityToken, identityBIG, secretBIG);

        }
        else if (disclosure.equals(Constants.ZKP_NI)) {
           return createProofForZKPNI(identityToken, identityBIG, secretBIG);
        }
        //PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(IDTResponse);
        return null;
    }

    public IdentityProof createInitialProofForZKPI(IdentityToken identityToken, BigInteger identityBIG, BigInteger secretBIG) throws JSONException, CryptoAlgorithmException {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_I);
        pedersenPublicParams = identityToken.getPedersenParams();
        ZKPK = new ZKPPedersenCommitment(pedersenPublicParams);
        helperCommitment = ZKPK.createHelperProblem(null);
        proof.addHelperCommitment(helperCommitment.getCommitment());
        return proof;
    }

    public IdentityProof createProofForZKPI(BigInteger challenge, BigInteger secretBIG, BigInteger emailBIG) {
        PedersenCommitment pedersenCommitment = new PedersenCommitment();
        pedersenCommitment.setX(emailBIG);
        pedersenCommitment.setR(secretBIG);
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_I);
        PedersenCommitmentProof commitmentProof = ZKPK.createProofForInteractiveZKP(pedersenCommitment, helperCommitment, challenge);
        proof.addProof(commitmentProof);
        return proof;
    }

    public IdentityProof createProofForZKPNI(IdentityToken identityToken, BigInteger secretBIG, BigInteger emailBIG) throws CryptoAlgorithmException, NoSuchAlgorithmException {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_NI);
        pedersenPublicParams = identityToken.getPedersenParams();
        ZKPK = new ZKPPedersenCommitment(pedersenPublicParams);
        List<PedersenCommitment> helperProblems = ZKPK.createHelperProblems(null);
        PedersenCommitment originalCommitmentInfo = new PedersenCommitment();
        originalCommitmentInfo.setCommitment(identityToken.getIdentityCommitment());
        List<BigInteger> challenges = ZKPK.createChallengeForNonInteractiveZKP(originalCommitmentInfo, helperProblems);
        originalCommitmentInfo.setX(emailBIG);
        originalCommitmentInfo.setR(secretBIG);
        List<PedersenCommitmentProof> proofs = ZKPK.createProofForNonInteractiveZKP(originalCommitmentInfo, helperProblems, challenges);

        //set the proof info in the identity proof obj
        //todo: from the name of the attribute in the identity token, identify the corresponding IDP access info and
        // get the username to be encrypted and included in the proof info.
        for (PedersenCommitment helperProblem : helperProblems) {
            proof.addHelperCommitment(helperProblem.getCommitment());
        }
        for (PedersenCommitmentProof pedersenCommitmentProof : proofs) {
            proof.addProof(pedersenCommitmentProof);
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        proof.setTimestampAtProofCreation(timestamp);
        return proof;
    }

    public IdentityProof createProofForZKPNIS() {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_NI_S);
        return null;
    }
}
