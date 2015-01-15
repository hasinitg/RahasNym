package org.rahasnym.spclient;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/16/14
 * Time: 1:55 PM
 */
public class SPClientConstants {
    //the port number that the IDMModule listens on
    public static String SP_URL = "http://localhost:8080/amazingshop/service/shop";
    public static int IDM_MODULE_PORT = 4444;
    public static String LOCAL_HOST = "localhost";
    public static String ZKP_I = "ZKP-I";

    public static final String USERID = "UserId";

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHAR_SET = "UTF-8";
    public static final int HTTP_ERROR_CODE = 500;

    /**
     * HTTP Request Methods used in RESTful communication
     */
    public enum RequestType{CREATE, UPDATE, REVOKE, GET};

    /*operation names and other info as compatible with policies.*/
    public static final String SIGN_UP_OPERATION = "sign_up";
    public static final String PASSWORD = "password";
}
