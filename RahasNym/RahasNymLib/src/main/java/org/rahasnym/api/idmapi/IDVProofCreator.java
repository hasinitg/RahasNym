package org.rahasnym.api.idmapi;

import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

import java.math.BigInteger;

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

    public IdentityProof createProof(IdentityToken identityToken, IDVPolicy combinedPolicy, BigInteger identityBIG, BigInteger secretBIG)
            throws JSONException, CryptoAlgorithmException {
        //look at the policy and create the appropriate proof
        IDVPolicy.Rule rule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet conditionSet = rule.getConditionSets().get(0);
        String disclosure = conditionSet.getDisclosure().get(0);
        if (disclosure.equals(Constants.ZKP_I)) {
            return createInitialProofForZKPI(identityToken, null, null);

        }
        if (disclosure.equals(Constants.ZKP_NI)) {

        }
        //PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(IDTResponse);
        return null;
    }

    public IdentityProof createInitialProofForZKPI(IdentityToken identityToken, BigInteger identityBIG, BigInteger secretBIG) throws JSONException, CryptoAlgorithmException {

        /*JSONObject root =  new JSONObject(new JSONTokener(IDTRequest));
        String challenge = root.optString(Constants.CHALLENGE);
        PedersenPublicParams params = new Util().extractPedersenParamsFromIDT(root);
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

        return jsonProof.toString();*/
        return null;
    }

    public IdentityProof createProofForZKPI(BigInteger challenge, BigInteger secretBIG, BigInteger emailBIG){
        return null;
    }

    public String createProofForZKPNI() {
        return null;
    }

    public String createProofForZKPNIS() {
        return null;
    }
}
