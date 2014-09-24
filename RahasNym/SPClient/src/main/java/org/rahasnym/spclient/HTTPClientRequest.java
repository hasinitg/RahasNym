package org.rahasnym.spclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/24/14
 * Time: 11:52 AM
 */
public class HTTPClientRequest {
    private Constants.RequestType reqType;
    Map<String, String> headers;
    String reqURI;
    String payload;
    int responseStatus;
    String response;


    public void setRequestType(Constants.RequestType requestType) {
        this.reqType = requestType;
    }


    public void setRequestHeaders(Map<String, String> headerParameters) {
        this.headers = headerParameters;
    }


    public void setRequestHeader(String headerParamName, String headerParamValue) {
        if (headers != null) {
            headers.put(headerParamName, headerParamValue);
        } else {
            headers = new HashMap<String, String>();
            headers.put(headerParamName, headerParamValue);
        }
    }


    public void setRequestURI(String requestURI) {
        this.reqURI = requestURI;
    }


    public void setPayLoad(String payload) {
        this.payload = payload;
    }


    private HttpMethod createRequest() throws UnsupportedEncodingException {
        //check if request type is set
        if (reqType != null) {
            //define the generic method type
            HttpMethod httpMethod = null;
            RequestEntity reqEntity;
            //initialize the specific HTTP method based on the request type
            switch (reqType) {
                case CREATE:
                    httpMethod = new PostMethod(reqURI);
                    //set payload
                    reqEntity = new StringRequestEntity(payload,
                            Constants.CONTENT_TYPE, Constants.CHAR_SET);
                    ((PostMethod) httpMethod).setRequestEntity(reqEntity);
                    break;

                case REVOKE:
                    httpMethod = new DeleteMethod(reqURI);
                    break;

                case UPDATE:
                    httpMethod = new PutMethod(reqURI);
                    //set payload
                    reqEntity = new StringRequestEntity(payload,
                            Constants.CONTENT_TYPE, Constants.CHAR_SET);
                    ((PutMethod) httpMethod).setRequestEntity(reqEntity);
                    break;

                case GET:
                    httpMethod = new GetMethod(reqURI);
                    break;

            }
            //set any request headers
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpMethod.addRequestHeader(entry.getKey(), entry.getValue());
                }
            }
            return httpMethod;

        } else {

            return null;
        }
    }


    public int execute() throws IOException {
        HttpClient httpClient = new HttpClient();
        HttpMethod request = createRequest();
        responseStatus = httpClient.executeMethod(request);
        response = request.getResponseBodyAsString();
        return responseStatus;
    }


    public String getResponseString() {
        return response;
    }


    public int getResponseStatus() {
        return responseStatus;
    }

}
