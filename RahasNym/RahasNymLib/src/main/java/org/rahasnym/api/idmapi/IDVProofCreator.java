package org.rahasnym.api.idmapi;

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.json.JSONObject;
import org.rahasnym.api.Constants;
import org.rahasnym.api.Util;
import org.rahasnym.api.communication.policy.IDVPolicy;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:58 PM
 */
public class IDVProofCreator {

    public String createProof(String IDTResponse, IDVPolicy combinedPolicy, BigInteger identityBIG, BigInteger secretBIG)
            throws JSONException {
        //look at the policy and create the appropriate proof
        IDVPolicy.Rule rule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet conditionSet = rule.getConditionSets().get(0);
        String disclosure = conditionSet.getDisclosure().get(0);
        if (disclosure.equals(Constants.ZKP_I)) {
            return IDTResponse;

        }
        //PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(IDTResponse);
        return null;
    }

    public String createProofForZKPI(JSONObject IDTRequest, BigInteger identityBIG, BigInteger secretBIG) throws JSONException, CryptoAlgorithmException {

        String challenge = IDTRequest.optString(Constants.CHALLENGE);
        PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(IDTRequest);
        ZKPPedersenCommitment zkpIDM = new ZKPPedersenCommitment(params);
        PedersenCommitment helperCommitment = zkpIDM.createHelperProblem(null);
        BigInteger challengeBIG = new BigInteger(challenge, 10);
        PedersenCommitment commitment = new PedersenCommitment();
        commitment.setX(identityBIG);
        commitment.setR(secretBIG);
        PedersenCommitmentProof proof = zkpIDM.createProofForInteractiveZKP(commitment, helperCommitment, challengeBIG);

        JSONObject jsonProof = new JSONObject();
        jsonProof.append(Constants.U_VALUE, proof.getU());
        jsonProof.append(Constants.V_VALUE, proof.getV());
        jsonProof.append(Constants.HELPER_COMMITMENT, helperCommitment);

        return jsonProof.toString();
    }

    public String createProofForZKPNI() {
        return null;
    }

    public String createProofForZKPNIS() {
        return null;
    }
}
