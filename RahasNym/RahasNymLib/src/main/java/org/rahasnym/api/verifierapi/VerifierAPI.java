package org.rahasnym.api.verifierapi;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.idenity.IdentityToken;

import java.io.IOException;
import java.text.ParseException;

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

    public String handleIDVReqMessage(String IDVReqMessage) throws JSONException, ParseException, CryptoAlgorithmException {
        //identify the request type
        //System.out.println("verifier heard from client: " + IDVReqMessage);
        JSONObject IDVResponse = new JSONObject(new JSONTokener(IDVReqMessage));
        String requestType = IDVResponse.optString(Constants.REQUEST_TYPE);
        IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
        //call identity verification handler which validates the token and verify the identity proof
        if (Constants.REQ_ZKP_I.equals(requestType)) {
            return verificationHandler.handleInitialZKPIRequest(IDVResponse);
        }
        if (Constants.AUTH_CHALLENGE_RESPONSE.equals(requestType)) {
            return verificationHandler.verifyZKPI(IDVResponse);
        }

        //return the response given by the Identity Verification Handler.
        return null;
    }

}
