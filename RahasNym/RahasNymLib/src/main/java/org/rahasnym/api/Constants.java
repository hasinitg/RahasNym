package org.rahasnym.api;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/6/14
 * Time: 9:36 PM
 */

/**
 * This encapsulates all the constants used throughout the RahasNymLib module.
 */
public class Constants {
    /*Policy constants*/
    public static String POLICY = "policy";
    public static String SPECIFIER_NAME = "specifierName";
    public static String SPECIFIER_ID = "specifierId";
    public static String RULE = "rule";
    public static String RULE_ID = "id";
    public static String TARGET = "target";
    public static String OVERRIDING_ALGORITHM = "overridingAlgorithm";
    public static String OPERATIONS = "operations";
    public static String SERVICE_PROVIDERS = "serviceProviders";
    public static String ALL = "all";
    public static String CONDITION_SET = "conditionSet";
    public static String APPLIES_TO = "appliesTo";
    public static String DISCLOSURE = "disclosure";
    public static String SUBJECT_VERIFICATION = "subjectVerification";
    public static String PSEUDONYM_CARDINALITY = "pseudonymCardinality";
    public static String EMPTY_STRING = "";

    public static String PLAIN_TEXT = "PLAIN_TEXT";
    public static String ZKP_I = "ZKP_I";
    public static String ZKP_NI = "ZKP_NI";
    public static String ZKP_NI_S = "ZKP_NI_S";
    public static String DEFAULT_DISCLOSURE_VALUE = "ZKP_I";

    public static String BIOMETRIC_BOUND = "BIOMETRIC_BOUND";
    public static String PSEUDONYM_BOUND = "PSEUDONYM_BOUND";
    public static String SP_BOUND = "SP_BOUND";
    public static String HIDDEN_PSEUDONYM_BOUND = "HIDDEN_PSEUDONYM_BOUND";
    public static String BIOMETRIC_BOUND_N_PSEUDONYM_BOUND = "BIOMETRIC_BOUND_N_PSEUDONYM_BOUND";
    public static String BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND = "BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND";
    public static String PSEUDONYM_BOUND_N_SP_BOUND = "PSEUDONYM_BOUND_N_SP_BOUND";
    public static String HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND = "HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND";
    public static String BIOMETRIC_BOUND_N_PSEUDONYM_BOUND_N_SP_BOUND = "BIOMETRIC_BOUND_N_PSEUDONYM_BOUND_N_SP_BOUND";
    public static String BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND = "BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND";

    public static String DEFAULT_SUBJECT_VERIFICATION_VALUE = "PSEUDONYM_BOUND_N_SP_BOUND";

    public static String MULTIPLE = "MULTIPLE";
    public static String SINGLE = "SINGLE";
    public static String DEFAULT_CARDINALITY_VALUE = "SINGLE";

    public static String SP_OVERRIDES = "SP_OVERRIDES";
    public static String CLIENT_OVERRIDES = "CLIENT_OVERRIDES";

    //the port number that the IDMModule listens on
    public static int IDM_MODULE_PORT = 4444;
    public static String LOCAL_HOST = "localhost";

    //default IDT Time-to-live is 20 mins.
    public static final long DEFAULT_IDT_TTL = 1200000;

    public static final String USER_ID = "UserId";

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHAR_SET = "UTF-8";

    //http response codes
    public static final int HTTP_ERROR_CODE = 500;
    public static final int CODE_OK = 200;
    /**
     * HTTP Request Methods used in RESTful communication
     */
    public enum RequestType{CREATE, UPDATE, REVOKE, GET};

    /*Client-To-IDMM request constants.*/
    public static final String OPERATION = "operation";
    public static final String VERIFIER_POLICY = "verifierPolicy";
    public static final String PSEUDONYM_WITH_SP = "pseudonymWithSP";
    public static final String REQUEST_TYPE = "reqType";
    public static final String IDT_REQUEST = "IDTRequest";
    public static final String CHALLENGE_MESSAGE = "challengeMessage";
    public static final String ACK_MESSAGE = "ackMessage";
    public static final String CHALLENGE = "challenge";
    public static final String TRANSACTION_RECEIPT = "transactionReceipt";

    /*Constants in IDT request from Client to IDP.*/
    public static final String SECRET = "secret";
    public static final String SP_IDENTITY = "spIdentity";
    public static final String SINGLE_PSEUDONYM_REQUIRED = "isSinglePseudonym";
    public static final String BIO_ID_REQUIRED = "isBioIDRequired";

    /*Constants in reply from IDP for IDT request.*/
    public static final String IDT = "identityToken";
    public static final String FROM = "from:";
    public static final String TO = "to:";
    public static final String ATTRIBUTE_NAME = "attributeName";
    public static final String IDENTITY_COMMITMENT = "identityCommitment";
    public static final String EXPIRATION_TIMESTAMP = "expirationTimestamp";
    public static final String CURRENT_TIMESTAMP = "currentTimestamp";
    public static final String BIOMETRIC_IDENTITY = "biometricIdentity";
    public static final String SINGLE_PSEUDONYM_CERTIFIED = "singlePseudonymCertified";
    public static final String PEDERSEN_PARAMS = "perdersenParams";
    public static final String P_PARAM = "P";
    public static final String Q_PARAM = "Q";
    public static final String G_PARAM = "G";
    public static final String H_PARAM = "H";
    public static final String SIGNATURE = "signature";
    public static final String PUBLIC_CERT = "publicCert";

    /*Constants in reply from IDMM to client in proof creation.*/
    public static final String U_VALUE = "U";
    public static final String V_VALUE = "V";
    public static final String HELPER_COMMITMENT = "helper";

    /*Error Messages*/
    public static final String REQUEST_ERROR = "Unidentified Request Type.";
}
