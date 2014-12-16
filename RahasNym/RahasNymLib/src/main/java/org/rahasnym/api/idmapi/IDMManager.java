package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 2:59 PM
 */

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.communication.policy.PolicyCombiner;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;
import org.rahasnym.api.verifierapi.ProofInfo;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

/**
 * This is responsible for processing the messages sent by client and delegate to the appropriate handler.
 */
public class IDMManager {

    private IDVPolicy combinedPolicy;
    private BigInteger secretBIG;
    private BigInteger emailBIG;
    private IDVProofCreator proofCreator;
    private IdentityMessagesEncoderDecoder encoderDecoder;

    public IDMManager() {
        proofCreator = new IDVProofCreator();
        encoderDecoder = new IdentityMessagesEncoderDecoder();
    }

    public String processClientMessage(String clientMessage) throws JSONException, IOException, RahasNymException,
            InvalidKeySpecException, NoSuchAlgorithmException, CryptoAlgorithmException, ParseException {
        JSONObject jsonRequest = new JSONObject(new JSONTokener(clientMessage));
        String reqType = jsonRequest.optString(Constants.REQUEST_TYPE);
        if (reqType.equals(Constants.IDT_REQUEST)) {
            return processIDTRequestMessage(jsonRequest);
        } else if (reqType.equals(Constants.AUTH_CHALLENGE)) {
            return processChallengeMessage(jsonRequest);
        } else if (reqType.equals(Constants.ACK_MESSAGE)) {
            return processAckMessage(jsonRequest);
        }
        return Constants.REQUEST_ERROR;
    }

    private String processIDTRequestMessage(JSONObject jsonRequest) throws JSONException, IOException, RahasNymException,
            InvalidKeySpecException, NoSuchAlgorithmException, CryptoAlgorithmException, ParseException {
        //read the operation name
        String operation = jsonRequest.getString(Constants.OPERATION);
        //get the verifier policy
        String spPolicy = jsonRequest.getString(Constants.VERIFIER_POLICY);
        //read the pseudonym used with the SP
        String pseudonymWithSP = jsonRequest.getString(Constants.PSEUDONYM_WITH_SP);

        //decode sp policy
        IDVPolicy spIDVPolicy = new JSONPolicyDecoder().decodePolicy(spPolicy);
        //combine policy
        PolicyCombiner policyCombiner = new PolicyCombiner();
        combinedPolicy = policyCombiner.getCombinedPolicy(spIDVPolicy, IDMMConfig.getInstance().getUserIDVPolicy(), operation);
        //TODO:obtain identity and user-password and create committable values out of them.
        String email = "hasinitg@gmail.com";
        emailBIG = CryptoUtil.getCommittableThruHash(email, CryptoLibConstants.SECRET_BIT_LENGTH);
        System.out.println("emailBIG at IDMM: " + emailBIG);
        //create committable secret from user-provided password.
        String password = "543&*^";
        byte[] salt = CryptoUtil.generateSalt(CryptoLibConstants.DEFAULT_LENGTH_OF_SALT);
        secretBIG = CryptoUtil.getCommittableThruPBKDF(password, salt, CryptoLibConstants.SECRET_BIT_LENGTH,
                CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);

        //request IDT
        IDTRequestSender IDTRequester = new IDTRequestSender();
        String IDTResponse = IDTRequester.requestIDT(combinedPolicy, secretBIG, pseudonymWithSP);
        IdentityToken idt = encoderDecoder.decodeIdentityToken(IDTResponse);
        //create proof adhering to policy
        //proofCreator = new IDVProofCreator();
        IdentityProof proof = proofCreator.createProof(idt, combinedPolicy, emailBIG, secretBIG);
        //todo: create the response message here
        String response = encoderDecoder.createIDTResponseByIDMM(IDTResponse, proof);
        return response;
    }

    private String processChallengeMessage(JSONObject challengeMessage) throws JSONException, CryptoAlgorithmException {
        //decode the challenge
        ProofInfo proofInfo = encoderDecoder.decodeChallengeMessage(challengeMessage);
        System.out.println("emailBIG at IDMM before challenge-response creation: " + emailBIG);
        IdentityProof proofResponse = proofCreator.createProofForZKPI(proofInfo.getChallengeValue(), emailBIG, secretBIG);
        String challengeResponse = encoderDecoder.createChallengeResponseByIDMM(proofInfo.getSessionID(), proofResponse);
        return challengeResponse;
    }

    private String processAckMessage(JSONObject jsonRequest) {
        //log the client response, whether it is a success or failure.
        return null;
    }

}
