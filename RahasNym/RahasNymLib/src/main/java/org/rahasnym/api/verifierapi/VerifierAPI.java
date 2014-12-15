package org.rahasnym.api.verifierapi;

import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;

import java.io.IOException;

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

    public String handleIDVReqMessage(String IDVReqMessage){
        //identify the request type

        //decode IDT and Proof

        //call identity verification handler which validates the token and verify the identity proof

        //return the response given by the Identity Verification Handler.
        return null;
    }

}
