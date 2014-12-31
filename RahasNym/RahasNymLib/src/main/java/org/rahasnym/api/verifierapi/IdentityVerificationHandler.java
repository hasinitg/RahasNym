package org.rahasnym.api.verifierapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:43 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.json.JSONObject;
import org.rahasnym.api.Constants;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This performs the verification part of each protocol.
 */
public class IdentityVerificationHandler {

    public String handleInitialZKPIRequest(JSONObject requestObject) throws JSONException, ParseException, CryptoAlgorithmException {

        JSONObject idt = (JSONObject) requestObject.opt(Constants.IDT);
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        IdentityToken identityToken = encoderDecoder.decodeIdentityTokenContent((JSONObject) requestObject.opt(Constants.IDT));
        IdentityProof identityProof = encoderDecoder.decodeIdentityProofContent(
                requestObject.optJSONObject(Constants.PROOF), Constants.ZKP_I);
        //todo: verify if the identity token is valid, based on signature verification.

        //create challenge
        ZKPPedersenCommitment zkp = new ZKPPedersenCommitment(identityToken.getPedersenParams());
        BigInteger challenge = zkp.createChallengeForInteractiveZKP();
        //create session-id
        String sessionID = UUID.randomUUID().toString();
        //create proof info and store
        ProofInfo proofInfo = new ProofInfo();
        proofInfo.setIdentityToken(identityToken);
        proofInfo.setIdentityProof(identityProof);
        proofInfo.setChallengeValue(challenge);
        proofInfo.setSessionID(sessionID);
        ProofStoreManager.getInstance().addProofInfo(sessionID, proofInfo);
        //create challenge message and send
        return encoderDecoder.encodeChallengeMessage(proofInfo);
    }

    public String verifyZKPI(JSONObject jsonProof) throws CryptoAlgorithmException, JSONException {
        //extract session id
        String sessionID = jsonProof.optString(Constants.SESSION_ID);
        //decode proof
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        IdentityProof identityProof = encoderDecoder.decodeChallengeResponse(jsonProof);
        //find previous info from proof store
        ProofInfo proofInfo = ProofStoreManager.getInstance().getProofInfo(sessionID);
        //verify the proof
        PedersenPublicParams params = proofInfo.getIdentityToken().getPedersenParams();
        ZKPPedersenCommitment ZKPK = new ZKPPedersenCommitment(params);
        PedersenCommitment helperCommitment = new PedersenCommitment();
        helperCommitment.setCommitment(proofInfo.getIdentityProof().getHelperCommitment());
        PedersenCommitment originalCommitment = new PedersenCommitment();
        originalCommitment.setCommitment(proofInfo.getIdentityToken().getIdentityCommitment());
        boolean verificationResult = ZKPK.verifyInteractiveZKP(originalCommitment, helperCommitment,
                proofInfo.getChallengeValue(), identityProof.getProof());

        return encoderDecoder.createAuthResultMessage(verificationResult);
    }

    public String verifyZKPNI(JSONObject jsonProof) throws JSONException, ParseException, CryptoAlgorithmException, NoSuchAlgorithmException {

        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();

        IdentityToken token = encoderDecoder.decodeIdentityTokenContent((JSONObject) jsonProof.opt(Constants.IDT));
        PedersenCommitment originalCommitment = new PedersenCommitment();
        originalCommitment.setCommitment(token.getIdentityCommitment());

        JSONObject proofContent = (JSONObject) jsonProof.opt(Constants.PROOF);
        IdentityProof proof = encoderDecoder.decodeIdentityProofContent(proofContent, Constants.ZKP_NI);
        List<BigInteger> challenges = proof.getChallenges();
        List<BigInteger> helperCommitments = proof.getHelperCommitments();
        List<PedersenCommitmentProof> proofs = proof.getProofs();
        List<PedersenCommitment> helperCommitmentsList = new ArrayList<>();
        for (BigInteger helperCommitment : helperCommitments) {
            PedersenCommitment commitment = new PedersenCommitment();
            commitment.setCommitment(helperCommitment);
            helperCommitmentsList.add(commitment);
        }
        PedersenPublicParams params = token.getPedersenParams();
        ZKPPedersenCommitment ZKPK = new ZKPPedersenCommitment(params);
        boolean result = ZKPK.verifyNonInteractiveZKP(originalCommitment, helperCommitmentsList, challenges, proofs);

        return encoderDecoder.createAuthResultMessage(result);
    }

    public String verifyZKPNIS(JSONObject jsonProof, String receipt) throws JSONException, ParseException, CryptoAlgorithmException, NoSuchAlgorithmException {
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();

        IdentityToken token = encoderDecoder.decodeIdentityTokenContent((JSONObject) jsonProof.opt(Constants.IDT));
        PedersenCommitment originalCommitment = new PedersenCommitment();
        originalCommitment.setCommitment(token.getIdentityCommitment());

        JSONObject proofContent = (JSONObject) jsonProof.opt(Constants.PROOF);

        IdentityProof proof = encoderDecoder.decodeIdentityProofContent(proofContent, Constants.ZKP_NI_S);
        List<BigInteger> challenges = proof.getChallenges();
        List<BigInteger> helperCommitments = proof.getHelperCommitments();
        List<PedersenCommitmentProof> proofs = proof.getProofs();
        List<PedersenCommitment> helperCommitmentsList = new ArrayList<>();
        for (BigInteger helperCommitment : helperCommitments) {
            PedersenCommitment commitment = new PedersenCommitment();
            commitment.setCommitment(helperCommitment);
            helperCommitmentsList.add(commitment);
        }
        PedersenPublicParams params = token.getPedersenParams();
        ZKPPedersenCommitment ZKPK = new ZKPPedersenCommitment(params);
        boolean result = ZKPK.verifyNonInteractiveZKPWithSignature(originalCommitment, helperCommitmentsList,
                receipt.getBytes(), challenges, proofs);

        return encoderDecoder.createAuthResultMessage(result);
    }

}
