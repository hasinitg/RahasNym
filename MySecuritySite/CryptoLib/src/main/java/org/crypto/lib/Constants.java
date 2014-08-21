package org.crypto.lib;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/17/14
 * Time: 11:13 AM
 */
public class Constants {
    public static String SHA256 = "SHA-256";
    /* Following defined algorithm constructs secret keys using the Password-Based Key Derivation Function
    function found in PKCS #5 v2.0. as per http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html*/
    public static String PBKDF5 = "PBKDF2WithHmacSHA1";
}
