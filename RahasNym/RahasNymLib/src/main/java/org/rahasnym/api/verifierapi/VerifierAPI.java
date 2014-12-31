package org.rahasnym.api.verifierapi;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RandomString;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityToken;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/9/14
 * Time: 1:14 PM
 */

/*This is the API to be used by SP in pseudonymous identity verification.*/

public class VerifierAPI {

    public String getIDVPolicy(String policyPath) throws IOException {
        return new JSONPolicyDecoder().readPolicyAsString(policyPath);
    }

    public String getIDVPolicyFromClassLoader(String policyPath) throws IOException {
        return new JSONPolicyDecoder().readPolicyAsStringFromClassLoader(policyPath);
    }

    public String getIReceipt() {
        String receipt = new RandomString().generateRandomString();
        return receipt;
    }


    public String handleIDVReqMessage(String IDVReqMessage, String receipt) throws JSONException, ParseException, CryptoAlgorithmException, NoSuchAlgorithmException {
        //identify the request type
        //System.out.println("verifier heard from client: " + IDVReqMessage);
        JSONObject IDVResponse = new JSONObject(new JSONTokener(IDVReqMessage));
        String requestType = IDVResponse.optString(Constants.REQUEST_TYPE);
        IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
        //call identity verification handler which validates the token and verify the identity proof
        if (Constants.REQ_ZKP_I.equals(requestType)) {
            return verificationHandler.handleInitialZKPIRequest(IDVResponse);
        } else if (Constants.AUTH_CHALLENGE_RESPONSE.equals(requestType)) {
            return verificationHandler.verifyZKPI(IDVResponse);
        } else if (Constants.REQ_ZKP_NI.equals(requestType)) {
            return verificationHandler.verifyZKPNI(IDVResponse);
        } else if (Constants.REQ_ZKP_NI_S.equals(requestType)){
            return verificationHandler.verifyZKPNIS(IDVResponse, receipt);
        }

        //return the response given by the Identity Verification Handler.
        return null;
    }

}
