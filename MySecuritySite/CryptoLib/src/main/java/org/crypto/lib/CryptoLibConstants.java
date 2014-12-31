package org.crypto.lib;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/17/14
 * Time: 11:13 AM
 */
public class CryptoLibConstants {
    public static String SHA256 = "SHA-256";
    public static String SHA512 = "SHA-512";
    /* Following defined algorithm constructs secret keys using the Password-Based Key Derivation Function
    function found in PKCS #5 v2.0. as per http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html*/
    public static String PBKDF5 = "PBKDF2WithHmacSHA1";
    public static int DEFAULT_PBKDF_ITERATIONS = 1000;
    public static int DEFAULT_LENGTH_OF_SALT = 8; //in bytes
    public static int NUMBER_OF_PROOFS_FOR_NI_ZKP = 3;
    public static final int SECRET_BIT_LENGTH = 150;
}
