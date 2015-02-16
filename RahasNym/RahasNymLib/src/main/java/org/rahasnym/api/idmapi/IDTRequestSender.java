package org.rahasnym.api.idmapi;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.HTTPClientRequest;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.idenity.IDTRequestMessage;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idpapi.RequestHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:46 PM
 */

/**
 * This creates the identity token request according to IDT Request Protocol and send to IDP along with auth info
 * for IDP.
 */
public class IDTRequestSender {

    public String requestIDT(IDVPolicy combinedPolicy, BigInteger secretBIG, String pseudoNymWithSP)
            throws CryptoAlgorithmException, NoSuchAlgorithmException, InvalidKeySpecException, JSONException, IOException {

        //TODO: here we assume that combined rule in the combined policy contains only one condition set for one particular attribute.
        //but there could be multiple attributes for a particular operation with multiple condition sets.
        IDVPolicy.Rule combinedRule = combinedPolicy.getRules().get(0);
        IDVPolicy.ConditionSet matchedCS = combinedRule.getConditionSets().get(0);
        List<String> attributes = matchedCS.getAppliesTo();
        String identityAttributeName = attributes.get(0);

        //String identityAttributeName = Constants.EMAIL_ATTRIBUTE;
        IDTRequestMessage reqMsg = new IDTRequestMessage();
        reqMsg.setAttributeName(identityAttributeName);
        reqMsg.setBiometricIdentityRequired(false);
        //todo: encrypt this with IDP's public key.
        reqMsg.setEncryptedSecret(secretBIG.toString());
        //todo: read the SP_ID from policy
        //todo: if pseudonym cardinality is single, send pseudonym and sp-identity in plain-text,
        //todo: if subject verification is: hidden-sp-bound/hidden-pseudonym-bound send them in hidden format.
        reqMsg.setSpIdentity(combinedPolicy.getSpecifierName());
        reqMsg.setPseudonym(pseudoNymWithSP);

        //todo: encode the message and send to IDT.
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        String encodedIDTReq = encoderDecoder.encodeIDTRequest(reqMsg);

        //for the moment, do a in-JVM call to IDP.
        /*RequestHandler IDP = new RequestHandler();
        String response = IDP.handleIDTRequest(encodedIDTReq, "hasini");*/
        HTTPClientRequest postR = new HTTPClientRequest();
        postR.setRequestType(Constants.RequestType.CREATE);
        IDPAccessInfo idpAccessInfo = IDMMConfig.getInstance().getIDPAccessInfo(identityAttributeName);
        postR.setRequestURI(idpAccessInfo.getUrl());
        postR.setRequestHeader(Constants.USER_NAME, idpAccessInfo.getUsername());
        postR.setPayLoad(encodedIDTReq);
        int status = postR.execute();
        return postR.getResponseString();
        //return response;
    }

    /*This is used for the purpose of testing protocol execution during build time.*/
    public String requestIDTInVM(IDVPolicy combinedPolicy, BigInteger secretBIG, String pseudoNymWithSP) throws RahasNymException, JSONException {
        //todo: identify the attribute, corresponding IDP and access information about it, and send the IDT request.
        String identityAttributeName = Constants.EMAIL_ATTRIBUTE;
        IDTRequestMessage reqMsg = new IDTRequestMessage();
        reqMsg.setAttributeName(identityAttributeName);
        reqMsg.setBiometricIdentityRequired(false);
        //todo: encrypt this with IDP's public key.
        reqMsg.setEncryptedSecret(secretBIG.toString());
        //todo: read the SP_ID from policy
        //todo: if pseudonym cardinality is single, send pseudonym and sp-identity in plain-text,
        //todo: if subject verification is: hidden-sp-bound/hidden-pseudonym-bound send them in hidden format.
        reqMsg.setSpIdentity("amazon.com");
        reqMsg.setPseudonym(pseudoNymWithSP);

        //todo: encode the message and send to IDT.
        IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
        String encodedIDTReq = encoderDecoder.encodeIDTRequest(reqMsg);

        RequestHandler IDP = new RequestHandler();

        return IDP.handleIDTRequest(encodedIDTReq, "hasini");
    }
}
