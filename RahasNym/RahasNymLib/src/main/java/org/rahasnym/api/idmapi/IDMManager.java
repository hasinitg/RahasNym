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
import org.rahasnym.api.idenity.AttributeCallBackManager;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;
import org.rahasnym.api.verifierapi.ProofInfo;
import org.rahasnym.api.verifierapi.ProofStoreManager;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.List;

/**
 * This is responsible for processing the messages sent by client and delegate to the appropriate handler.
 */
public class IDMManager {

    private IDVPolicy combinedPolicy;
    private BigInteger secretBIG;
    private BigInteger identityBIG;
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
        } else if (reqType.equals(Constants.AUTH_CHALLENGE_EXTERNAL)) {
            return processChallengeFromExternalClient(jsonRequest);
        } else if (reqType.equals(Constants.AUTH_RESULT)) {
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
        //read receipt if available
        String receipt = jsonRequest.optString(Constants.TRANSACTION_RECEIPT);
        //read session id if available
        String sessionID = jsonRequest.optString(Constants.SESSION_ID);
        //decode sp policy
        IDVPolicy spIDVPolicy = new JSONPolicyDecoder().decodePolicy(spPolicy);
        //combine policy
        PolicyCombiner policyCombiner = new PolicyCombiner();
        combinedPolicy = policyCombiner.getCombinedPolicy(spIDVPolicy, IDMMConfig.getInstance().getUserIDVPolicy(), operation);

        //create committable secret from user-provided password.
        String password = PasswordCallBackManager.getPasswordOfCurrentUser();
        //String password = "543&*^";
        byte[] salt = CryptoUtil.generateSalt(CryptoLibConstants.DEFAULT_LENGTH_OF_SALT);
        secretBIG = CryptoUtil.getCommittableThruPBKDF(password, salt, CryptoLibConstants.SECRET_BIT_LENGTH,
                CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);

        //request IDT
        IDTRequestSender IDTRequester = new IDTRequestSender();
        String IDTResponse = null;
        if ((jsonRequest.optString(Constants.IS_IN_VM) != null) && (!(jsonRequest.optString(Constants.IS_IN_VM).equals("")))) {
            //this is for the purpose of running test cases during build time.
            IDTResponse = IDTRequester.requestIDTInVM(combinedPolicy, secretBIG, pseudonymWithSP);
        } else {
            IDTResponse = IDTRequester.requestIDT(combinedPolicy, secretBIG, pseudonymWithSP);
        }
        IdentityToken idt = encoderDecoder.decodeIdentityToken(IDTResponse);

        //create proof adhering to policy
        //String email = "hasinitg@gmail.com";

        String userName = PasswordCallBackManager.getNameOfCurrentUser();
        String identityValue = AttributeCallBackManager.getAttributeValue(userName, idt.getAttributeName());
        identityBIG = CryptoUtil.getCommittableThruHash(identityValue, CryptoLibConstants.SECRET_BIT_LENGTH);
        IdentityProof proof = proofCreator.createProof(idt, combinedPolicy, identityBIG, secretBIG, receipt);
        //todo: create the response message here
        String response = encoderDecoder.createIDTResponseByIDMM(IDTResponse, proof, proof.getSessionID());
        return response;
    }

    //this method is used if the protocol is executed by RahasNymClientAPI and IDMMAPI
    private String processChallengeMessage(JSONObject challengeMessage) throws JSONException, CryptoAlgorithmException {
        //decode the challenge
        ProofInfo proofInfo = encoderDecoder.decodeChallengeMessage(challengeMessage);
        //System.out.println("identityBIG at IDMM before challenge-response creation: " + identityBIG);
        //get the secretBIG and identityBIG from proofstore
        IdentityProof proofResponse = proofCreator.createProofForZKPI(proofInfo.getChallengeValue(), secretBIG, identityBIG);
        //todo: handling session id is not IDMM's responsibility. move it to Client API.
        String challengeResponse = encoderDecoder.createChallengeResponseByIDMM(proofInfo.getSessionID(), proofResponse);

        return challengeResponse;
    }

    /**
     * this method is used if the protocol is executed by an external client and RahasNym IDMM API where the state is
     * not preserved in RahasNym IDMM
     */
    private String processChallengeFromExternalClient(JSONObject challenge) throws JSONException, CryptoAlgorithmException {
        //decode the challenge
        ProofInfo proofInfo = encoderDecoder.decodeChallengeMessage(challenge);
        ProofInfo storedProofInfo = ProofStoreManager.getInstance().getProofInfo(proofInfo.getSessionID());
        storedProofInfo.setChallengeValue(proofInfo.getChallengeValue());
        //System.out.println("identityBIG at IDMM before challenge-response creation: " + identityBIG);
        //get the secretBIG and identityBIG from proofstore
        IdentityProof proofResponse = proofCreator.createProofForZKPI(storedProofInfo);
        String challengeResponse = encoderDecoder.createChallengeResponseByIDMM(proofResponse);

        return challengeResponse;
    }

    private String processAckMessage(JSONObject jsonRequest) {
        //log the client response, whether it is a success or failure.
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        String result = encoderDecoder.decodeAuthResultContent(jsonRequest);
        //TODO: provide a call back handler for the IDMM developper to register an impl for it and get the auth result if needed.
        //so that they can do whatever with that result.
        return null;
    }

}
