package org.rahasnym.serviceprovider;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.JAXRSResponseBuilder;
import org.rahasnym.api.communication.RahasNymResponse;
import org.rahasnym.api.verifierapi.VerifierAPI;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/13/13
 * Time: 6:57 PM
 */
@Path("/")
public class AmazingShop {
    @POST
    public Response verifyIdentity(String message){
        try {
            VerifierAPI verifierAPI = new VerifierAPI();
            String response = verifierAPI.handleIDVReqMessage(message);
            RahasNymResponse resp = new RahasNymResponse(Constants.CODE_OK, response);
            return new JAXRSResponseBuilder().buildResponse(resp);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (ParseException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        }
        //System.out.println("AmazingShop Service Invoked...");
    }

    @GET
    public Response getIDVPolicy() {
        //todo:read policy from config file and keep it in a global place coz it is required at  the verification as well.
        //String idvPolicyPath = "/home/hasini/Hasini/Experimenting/RahasNym/RahasNymLib/src/test/java/org/rahasnym/api/policies/clientPolicy";
        try {
            String policy = SPConfig.getInstance().getSpPolicyString();
            //String policy = new VerifierAPI().getIDVPolicy(idvPolicyPath);
            RahasNymResponse resp = new RahasNymResponse(Constants.CODE_OK, policy);
            return new JAXRSResponseBuilder().buildResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(
                    new RahasNymResponse(Constants.HTTP_ERROR_CODE, e.getMessage()));
        }
    }



}
