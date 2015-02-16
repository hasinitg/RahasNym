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
import org.rahasnym.api.verifierapi.ProofInfo;
import org.rahasnym.api.verifierapi.ProofStoreManager;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public IdentityProof createProof(IdentityToken identityToken, IDVPolicy combinedPolicy, BigInteger identityBIG,
                                     BigInteger secretBIG, String receipt)
            throws JSONException, CryptoAlgorithmException, NoSuchAlgorithmException {
        //look at the policy and create the appropriate proof
        //TODO:here we assume that the combined rule contains only one condition set for one attribute.
        //but there could be multiple condition sets for multiple attributes. It should be handled by the IDMM.
        IDVPolicy.Rule rule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet conditionSet = rule.getConditionSets().get(0);
        String disclosure = conditionSet.getDisclosure().get(0);
        if (disclosure.equals(Constants.ZKP_I)) {
            return createInitialProofForZKPI(identityToken, identityBIG, secretBIG);

        } else if (disclosure.equals(Constants.ZKP_NI)) {
            return createProofForZKPNI(identityToken, secretBIG, identityBIG);
        } else if (disclosure.equals(Constants.ZKP_NI_S)) {
            return createProofForZKPNIS(identityToken, secretBIG, identityBIG, receipt);
        }
        //PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(IDTResponse);
        return null;
    }

    public IdentityProof createInitialProofForZKPI(IdentityToken identityToken, BigInteger identityBIG, BigInteger secretBIG)
            throws JSONException, CryptoAlgorithmException {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_I);
        pedersenPublicParams = identityToken.getPedersenParams();
        ZKPK = new ZKPPedersenCommitment(pedersenPublicParams);
        helperCommitment = ZKPK.createHelperProblem(null);
        proof.addHelperCommitment(helperCommitment.getCommitment());

        String sessionID = UUID.randomUUID().toString();
        ProofInfo proofInfo = new ProofInfo();
        //add session id to identity proof
        proof.setSessionID(sessionID);
        proofInfo.setSessionID(sessionID);
        proofInfo.setIdentityToken(identityToken);
        proofInfo.setFullHelperCommitment(helperCommitment);
        proofInfo.setsValue(identityBIG);
        proofInfo.setrValue(secretBIG);

        ProofStoreManager.getInstance().addProofInfo(sessionID, proofInfo);
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

    public IdentityProof createProofForZKPI(ProofInfo proofinfo) throws CryptoAlgorithmException {
        PedersenCommitment pedersenCommitment = new PedersenCommitment();
        pedersenCommitment.setX(proofinfo.getsValue());
        pedersenCommitment.setR(proofinfo.getrValue());
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_I);
        ZKPK = new ZKPPedersenCommitment(proofinfo.getIdentityToken().getPedersenParams());
        PedersenCommitmentProof commitmentProof = ZKPK.createProofForInteractiveZKP(pedersenCommitment,
                proofinfo.getFullHelperCommitment(), proofinfo.getChallengeValue());
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

        List<PedersenCommitmentProof> zkProofs = ZKPK.createProofForNonInteractiveZKP(
                originalCommitmentInfo, helperProblems, challenges);

        proof.setChallenges(challenges);
        //set the proof info in the identity proof obj
        //todo: from the name of the attribute in the identity token, identify the corresponding IDP access info and
        // get the username to be encrypted and included in the proof info.
        for (PedersenCommitment helperProblem : helperProblems) {
            proof.addHelperCommitment(helperProblem.getCommitment());
        }
        for (PedersenCommitmentProof pedersenCommitmentProof : zkProofs) {
            proof.addProof(pedersenCommitmentProof);
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        proof.setTimestampAtProofCreation(timestamp);
        return proof;
    }

    public IdentityProof createProofForZKPNIS(IdentityToken identityToken, BigInteger secretBIG, BigInteger emailBIG,
                                              String receipt) throws CryptoAlgorithmException, NoSuchAlgorithmException {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_NI_S);

        pedersenPublicParams = identityToken.getPedersenParams();
        ZKPK = new ZKPPedersenCommitment(pedersenPublicParams);

        List<PedersenCommitment> helperProblems = ZKPK.createHelperProblems(null);

        PedersenCommitment originalCommitmentInfo = new PedersenCommitment();
        originalCommitmentInfo.setCommitment(identityToken.getIdentityCommitment());

        List<BigInteger> challenges = ZKPK.createChallengeForNonInteractiveZKPWithSignature(originalCommitmentInfo,
                helperProblems, receipt.getBytes());

        originalCommitmentInfo.setX(emailBIG);
        originalCommitmentInfo.setR(secretBIG);

        List<PedersenCommitmentProof> zkProofs = ZKPK.createProofForNonInteractiveZKP(originalCommitmentInfo,
                helperProblems, challenges);

        proof.setChallenges(challenges);
        //set the proof info in the identity proof obj
        //todo: from the name of the attribute in the identity token, identify the corresponding IDP access info and
        // get the username to be encrypted and included in the proof info.
        for (PedersenCommitment helperProblem : helperProblems) {
            proof.addHelperCommitment(helperProblem.getCommitment());
        }
        for (PedersenCommitmentProof pedersenCommitmentProof : zkProofs) {
            proof.addProof(pedersenCommitmentProof);
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        proof.setTimestampAtProofCreation(timestamp);
        return proof;
    }
}
