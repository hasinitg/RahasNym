package org.rahasnym.api.communication;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/9/14
 * Time: 2:08 PM
 */
public class RahasNymResponse {
    private int responseCode;
    private String responseString;
    private Map<String, String> headerParamMap;

    public RahasNymResponse(int responseCode, String responseString) {
        this.responseCode = responseCode;
        this.responseString = responseString;
    }

    public RahasNymResponse(int responseCode, String responseString, Map<String, String> paramMap) {
        this.responseCode = responseCode;
        this.responseString = responseString;
        this.headerParamMap = paramMap;
    }

    public Map<String, String> getHeaderParamMap() {
        return headerParamMap;
    }

    public void setHeaderParamMap(Map<String, String> headerParamMap) {
        this.headerParamMap = headerParamMap;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
