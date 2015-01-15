package org.rahasnym.serviceprovider;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.Util;
import org.rahasnym.api.communication.JAXRSResponseBuilder;
import org.rahasnym.api.communication.RahasNymResponse;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.idenity.IdentityToken;
import org.rahasnym.api.verifierapi.ProofInfo;
import org.rahasnym.api.verifierapi.ProofStore;
import org.rahasnym.api.verifierapi.ProofStoreManager;
import org.rahasnym.api.verifierapi.VerifierAPI;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/13/13
 * Time: 6:57 PM
 */
@Path("/")
public class AmazingShop {
    @POST
    public Response verifyIdentity(String message) {
        try {
            //decode message and see if there is a receipt
            JSONObject jsonMsg = new JSONObject(new JSONTokener(message));
            String reqType = jsonMsg.optString(Constants.REQUEST_TYPE);
            String receipt = null;
            if (Constants.REQ_ZKP_NI_S.equals(reqType)) {
                String sessionID = jsonMsg.optString(Constants.SESSION_ID);
                receipt = SPConfig.getInstance().retrieveReceipt(sessionID);
            }

            IdentityToken identityToken = null;
            if ((Constants.REQ_ZKP_I.equals(reqType)) || (Constants.REQ_ZKP_NI.equals(reqType)) ||
                    (Constants.REQ_ZKP_NI_S.equals(reqType))) {
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                identityToken = encoderDecoder.decodeIdentityToken(message);
            }
            if (identityToken != null) {
                ProofInfo proofInfo = new ProofInfo();
                String sessionID = UUID.randomUUID().toString();
                proofInfo.setSessionID(sessionID);
                proofInfo.setIdentityToken(identityToken);
            }

            VerifierAPI verifierAPI = new VerifierAPI();
            //Todo: in addition to the below inputs, verifier's policy should also be passed into the below method.
            String response = verifierAPI.handleIDVReqMessage(message, receipt);
            //TODO:in addition to the auth result returned by the API, SP can append additional details such as session-id etc

            //decode response and see if IDV was successful. If so, record it and add a session id if not present already.
            JSONObject jsonRespObj = new JSONObject(new JSONTokener(response));
            String respType = jsonRespObj.optString(Constants.REQUEST_TYPE);
            if (Constants.AUTH_RESULT.equals(respType)) {
                String authResult = jsonRespObj.optString(Constants.VERIFICATION_RESULT);
                if (Constants.AUTH_SUCCESS.equals(authResult)) {
                    //ProofStoreManager.getInstance().addProofInfo();
                }
            }
            //for the user to carry out further operations.
            RahasNymResponse resp = new RahasNymResponse(Constants.CODE_OK, response);
            return new JAXRSResponseBuilder().buildResponse(resp);
        } catch (JSONException e) {
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (IOException e) {
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (RahasNymException e) {
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (ParseException e) {
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        }
    }

    @GET
    public Response getIDVPolicy(@HeaderParam(Constants.RECEIPT_REQUIRED) String receiptReguired) {
        //todo:read policy from config file and keep it in a global place coz it is required at  the verification as well.
        //String idvPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy";
        try {
            JSONObject response = new JSONObject();
            String policy = SPConfig.getInstance().getSpPolicyString();
            response.put(Constants.POLICY, policy);
            if (receiptReguired != null) {
                if (Constants.TRUE.equals(receiptReguired)) {
                    String receiptNSID = SPConfig.getInstance().issueReceipt();
                    String[] rs = receiptNSID.split(",");
                    response.put(Constants.SESSION_ID, rs[0]);
                    response.put(Constants.TRANSACTION_RECEIPT, rs[1]);
                }
            }
            //String policy = new VerifierAPI().getIDVPolicy(idvPolicyPath);
            RahasNymResponse resp = new RahasNymResponse(Constants.CODE_OK, response.toString());
            return new JAXRSResponseBuilder().buildResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        }
    }
}
