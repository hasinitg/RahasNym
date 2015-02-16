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

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/19/15
 * Time: 2:24 PM
 */
@Path("/")
public class SIDVerificationServlet {
    @POST
    public Response getFreeShippingMembership(@HeaderParam(SPConstants.LOGGED_IN_SESSION_ID) String loggedInSessionID, String message) {
        //check if the request is by a logged in user
        if (!UserStore.getInstance().isLoggedIn(loggedInSessionID)) {
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE, "Please login first."));
        }
        //process request
        System.out.println("Get free shipping membership called with message: " + message);
        try {
            JSONObject jsonReq = new JSONObject(new JSONTokener(message));

            //get the user name
            String userName = jsonReq.optString(Constants.USER_NAME);

            //check which type of request is this

            String requestType = jsonReq.optString(Constants.REQUEST_TYPE);
            if (Constants.REQ_ZKP_I.equals(requestType)) {

                //get the IDT and proof
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                IdentityToken identityToken = encoderDecoder.decodeIdentityToken(message);

                //check if username and IDT's pseudonym matches
                if ((userName == null) || (!(userName.equals(identityToken.getPseudoNym())))) {
                    return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE,
                            "User name doesn't match with IDT."));
                }

                IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
                String authResponse = verificationHandler.handleInitialZKPIRequest(jsonReq);
                return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.CODE_OK, authResponse.toString()));

            }
            if (Constants.AUTH_CHALLENGE_RESPONSE.equals(requestType)) {
                IdentityVerificationHandler verificationHandler = new IdentityVerificationHandler();
                String verificationResponse = verificationHandler.verifyZKPI(jsonReq);
                JSONObject verificationObj = new JSONObject(new JSONTokener(verificationResponse));
                String verificationResult = verificationObj.optString(Constants.VERIFICATION_RESULT);

                if (Constants.AUTH_SUCCESS.equals(verificationResult)) {
                    //save the user's free shipping membership upon successful IDT verification.
                    UserStore.getInstance().getUser(userName).setFreeShippingEnabled(true);
                }

                return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.CODE_OK, verificationResponse));
            }
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(Constants.HTTP_ERROR_CODE, "Unexpected request type."));

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
