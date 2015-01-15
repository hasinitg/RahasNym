package org.rahasnym.serviceprovider;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.JAXRSResponseBuilder;
import org.rahasnym.api.communication.RahasNymResponse;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityToken;
import org.rahasnym.api.verifierapi.IdentityVerificationHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/10/15
 * Time: 2:50 PM
 */
@Path("/")
public class SignUpServlet {
    @POST
    public Response signup(String message) throws URISyntaxException {
        //do signup processing
        System.out.println("Sign up called with message: " + message);
        try {
            JSONObject jsonReq = new JSONObject(new JSONTokener(message));

            //get the user name
            String userName = jsonReq.optString(Constants.USER_NAME);

            //get the IDT and proof
            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            IdentityToken identityToken = encoderDecoder.decodeIdentityToken(message);

            //check if username and IDT's pseudonym matches
            if ((userName == null) || (!(userName.equals(identityToken.getPseudoNym())))) {
                return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE,
                        "User name doesn't match with IDT."));
            }

            //verify idt and proof
            IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
            String authResponse = verificationHandler.verifyZKPNI(jsonReq);
            String authSuccess = encoderDecoder.decodeAuthResult(authResponse);

            String sessionID = null;
            if (Constants.AUTH_SUCCESS.equals(authSuccess)) {
                //save the user upon successful IDT verification.
                sessionID = UUID.randomUUID().toString();
                User user = new User();
                user.setUserName(userName);
                user.setEmailIDT(identityToken);
                //todo: hash the password and save
                user.setHashPassword(jsonReq.optString(SPConstants.PASSWORD));
                UserStore.getInstance().saveEnrolledUser(user);
                UserStore.getInstance().addJustSignedUp(sessionID);
            }
            System.out.println("sending session id: " + sessionID);
            //add session id to auth response
            JSONObject jsonAuthResp = new JSONObject(authResponse);
            jsonAuthResp.put(Constants.SESSION_ID, sessionID);
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.CODE_OK, jsonAuthResp.toString()));
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (ParseException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE,
                    "Error in decoding sign up request."));
        } catch (RahasNymException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE, "Error in verifying identity."));
        }
    }
}
