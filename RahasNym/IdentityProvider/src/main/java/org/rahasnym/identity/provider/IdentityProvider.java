package org.rahasnym.identity.provider;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.json.JSONException;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.JAXRSResponseBuilder;
import org.rahasnym.api.communication.RahasNymResponse;
import org.rahasnym.api.idpapi.RequestHandler;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/18/14
 * Time: 3:56 PM
 */
@Path("/")
public class IdentityProvider {
    @POST
    public Response getIdentityToken(@HeaderParam(Constants.USER_NAME) String userName, String payload) {
        RequestHandler idpAPI = new RequestHandler();
        try {
            String response = idpAPI.handleIDTRequest(payload, userName);
            RahasNymResponse resp = new RahasNymResponse(Constants.CODE_OK, response);
            return new JAXRSResponseBuilder().buildResponse(resp);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(
                    Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (CryptoAlgorithmException e) {
            //e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(
                    Constants.HTTP_ERROR_CODE, e.getMessage()));
        } catch (JSONException e) {
            //e.printStackTrace();
            return new JAXRSResponseBuilder().buildResponse(new RahasNymResponse(
                    Constants.HTTP_ERROR_CODE, e.getMessage()));
        }
    }
}
