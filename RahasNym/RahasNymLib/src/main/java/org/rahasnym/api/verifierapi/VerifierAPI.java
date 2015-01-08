package org.rahasnym.api.verifierapi;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
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

    /*public String getIDVPolicy(String policyPath) throws IOException {
        return new JSONPolicyDecoder().readPolicyAsString(policyPath);
    }*/

    public String getIDVPolicyFromClassLoader(String policyPath) throws IOException {
        return new JSONPolicyDecoder().readPolicyAsStringFromClassLoader(policyPath);
    }

    /*public String getIReceipt() {
        String receipt = new RandomString().generateRandomString();
        return receipt;
    }*/

    /*Todo: in addition to the below inputs, verifier policy should also be passed into this method.*/
    public String handleIDVReqMessage(String IDVReqMessage, String receipt) throws RahasNymException {
        //todo:validate the contents in the identity token and proof against the verifier policy.
        //todo: perform the common set of operations in validating IDT in any protocol. such as validating against policy,
        //signature verification and expiration timestamp verification.
        //identify the request type
        //System.out.println("verifier heard from client: " + IDVReqMessage);
        try {

            JSONObject IDVReq = new JSONObject(new JSONTokener(IDVReqMessage));
            String requestType = IDVReq.optString(Constants.REQUEST_TYPE);
            IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
            //call identity verification handler which validates the token and verify the identity proof
            if (Constants.REQ_ZKP_I.equals(requestType)) {
                return verificationHandler.handleInitialZKPIRequest(IDVReq);
            } else if (Constants.AUTH_CHALLENGE_RESPONSE.equals(requestType)) {
                return verificationHandler.verifyZKPI(IDVReq);
            } else if (Constants.REQ_ZKP_NI.equals(requestType)) {
                return verificationHandler.verifyZKPNI(IDVReq);
            } else if (Constants.REQ_ZKP_NI_S.equals(requestType)) {
                return verificationHandler.verifyZKPNIS(IDVReq, receipt);
            }
            throw new RahasNymException("Un-identified identity verification request.");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in decoding the identity verification request.");
        }
    }

}
