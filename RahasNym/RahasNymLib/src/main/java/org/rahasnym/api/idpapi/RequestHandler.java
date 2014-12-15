package org.rahasnym.api.idpapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:44 PM
 */

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.rahasnym.api.idenity.IDTRequestMessage;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityToken;

import java.security.NoSuchAlgorithmException;

/**
 * This handles the requests sent to IDP.
 */
public class RequestHandler {
    /**
     * This handles the identity token request.
     */
    public String handleIDTRequest(String requestMessage, String userID) throws NoSuchAlgorithmException, CryptoAlgorithmException, JSONException {
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        //decode the request message
        IDTRequestMessage IDTReq = encoderDecoder.decodeIDTRequestMessage(requestMessage);
        //pass it to identity token factory
        IDTTokenFactory IDTFac = new IDTTokenFactory();
        IdentityToken IDT = IDTFac.createIdentityToken(IDTReq, userID);
        //encode the identity token and return
        String encodedIDT = encoderDecoder.encodeIdentityToken(IDT);
        return encodedIDT;
    }

    public String handleEnrollmentRequest(String message, String userID){
        return null;
    }

}
