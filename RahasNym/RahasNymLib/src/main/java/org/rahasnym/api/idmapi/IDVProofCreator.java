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
            throws JSONException, CryptoAlgorithmException {
        //look at the policy and create the appropriate proof
        IDVPolicy.Rule rule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet conditionSet = rule.getConditionSets().get(0);
        String disclosure = conditionSet.getDisclosure().get(0);
        if (disclosure.equals(Constants.ZKP_I)) {
            return createInitialProofForZKPI(identityToken, identityBIG, secretBIG);

        }
        if (disclosure.equals(Constants.ZKP_NI)) {

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

    public IdentityProof createProofForZKPNI() {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_NI);
        return null;
    }

    public IdentityProof createProofForZKPNIS() {
        IdentityProof proof = new IdentityProof();
        proof.setProofType(Constants.ZKP_NI_S);
        return null;
    }
}
