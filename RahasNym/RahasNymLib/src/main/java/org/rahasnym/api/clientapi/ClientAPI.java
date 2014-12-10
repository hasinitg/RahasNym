package org.rahasnym.api.clientapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/6/14
 * Time: 6:18 PM
 */

import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.HTTPClientRequest;

import java.io.IOException;

/**
 * This is the API provided for the developers of verifier client - i.e: SPClient.
 */
public class ClientAPI {
    /*Request the identity verification policy from the SP*/
    public String requestPolicy(String url) {
        try {
            HTTPClientRequest getR = new HTTPClientRequest();
            getR.setRequestType(Constants.RequestType.GET);
            getR.setRequestURI(url);
            int status = getR.execute();
            //todo: check if it is a success status, if so, obtain the policy from the string
            String responseString = getR.getResponseString();
            System.out.println(responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String requestToken() {
        return null;
    }

    public String submitToken() {
        return null;
    }

}
