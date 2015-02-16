package org.rahasnym.api.verifierapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:43 PM
 */

import org.crypto.lib.PKC.RSA.SignerVerifier;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.json.JSONObject;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This performs the verification part of each protocol.
 */
public class IdentityVerificationHandler {

    public String handleInitialZKPIRequest(JSONObject requestObject) throws RahasNymException {
        try {
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            IdentityToken identityToken = encoderDecoder.decodeIdentityTokenContent((JSONObject) requestObject.opt(Constants.IDT));
            verifySignatureOnIDT(identityToken);
            IdentityProof identityProof = encoderDecoder.decodeIdentityProofContent(
                    requestObject.optJSONObject(Constants.PROOF), Constants.ZKP_I);

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
            proofInfo.setVerificationStatus(Constants.IDENTITY_VERIFICATION_PENDING);
            ProofStoreManager.getInstance().addProofInfo(sessionID, proofInfo);
            //create challenge message and send
            return encoderDecoder.encodeChallengeMessage(proofInfo);

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding the JSON message.");
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in creating the challenge.");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding the identity proof.");
        }
    }

    public String verifyZKPI(JSONObject jsonProof) throws RahasNymException {
        try {
            //extract session id
            String sessionID = jsonProof.optString(Constants.SESSION_ID);
            //decode proof
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            IdentityProof identityProof = encoderDecoder.decodeChallengeResponse(jsonProof);
            //find previous info from proof store
            ProofInfo proofInfo = ProofStoreManager.getInstance().getProofInfo(sessionID);
            if (proofInfo != null) {
                //verify the proof
                PedersenPublicParams params = proofInfo.getIdentityToken().getPedersenParams();
                ZKPPedersenCommitment ZKPK = new ZKPPedersenCommitment(params);
                PedersenCommitment helperCommitment = new PedersenCommitment();
                helperCommitment.setCommitment(proofInfo.getIdentityProof().getHelperCommitment());
                PedersenCommitment originalCommitment = new PedersenCommitment();
                originalCommitment.setCommitment(proofInfo.getIdentityToken().getIdentityCommitment());
                boolean verificationResult = ZKPK.verifyInteractiveZKP(originalCommitment, helperCommitment,
                        proofInfo.getChallengeValue(), identityProof.getProof());
                ProofStoreManager.getInstance().removeProofInfo(sessionID);
                return encoderDecoder.createAuthResultMessage(verificationResult);
            } else {
                throw new RahasNymException("No previous records of this proof found.");
            }
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying the zero knowledge proof.");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in encoding the auth result");
        }
    }

    public String verifyZKPNI(JSONObject jsonReq) throws RahasNymException {
        try {
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();

            IdentityToken token = encoderDecoder.decodeIdentityTokenContent((JSONObject) jsonReq.opt(Constants.IDT));
            verifySignatureOnIDT(token);
            PedersenCommitment originalCommitment = new PedersenCommitment();
            originalCommitment.setCommitment(token.getIdentityCommitment());

            JSONObject proofContent = (JSONObject) jsonReq.opt(Constants.PROOF);
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
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding identity proof.");
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying identity proof.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying identity proof.");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding identity token or identity proof.");
        }
    }

    public String verifyZKPNIS(JSONObject jsonProof, String receipt) throws RahasNymException {
        try {
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();

            IdentityToken token = encoderDecoder.decodeIdentityTokenContent((JSONObject) jsonProof.opt(Constants.IDT));
            verifySignatureOnIDT(token);
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
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding identity proof.");
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying identity proof.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying identity proof.");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding identity token or identity proof.");
        }
    }

    public void verifySignatureOnIDT(IdentityToken identityToken) throws RahasNymException {
        try {
            String concatenatedInfo = new IdentityMessagesEncoderDecoder().getConcatenatedInfoFromIDT(identityToken);
            SignerVerifier verifier = new SignerVerifier();
            Certificate publicCert = VerifierCallBackManager.getTrustedCert(identityToken.getPublicCertAlias());

            boolean sigVerified = verifier.verifySignature(concatenatedInfo, identityToken.getSignature(), publicCert);

            if (!sigVerified) {
                throw new RahasNymException("Signature verification on the identity token failed.");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying the signature on IDT");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying the signature on IDT");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying the signature on IDT");
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in verifying the signature on IDT");
        }
    }

}
