package org.rahasnym.serviceprovider;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/13/13
 * Time: 11:21 PM
 */
public class SPConstants {

    public static final String PURDUE_ID_COMMITMENT = "PurdueIDCommitment";
    public static final String HEALTH_INSURANCE_ID_COMMITMENT = "HealthInsuranceIDCommitment";
    public static final String BIOMETRIC_ID_COMMITMENT = "BIOMETRIC_ID_COMMITMENT";
    public static final String USER_ID = "USER_ID";

    /*operation names and other info as compatible with policies.*/
    public static final String SIGN_UP_OPERATION = "sign_up";
    public static final String PASSWORD = "password";

    public enum SignUpStatus {
        SIGNUP_SUCCESS, SIGNUP_FAILURE

    }
}
