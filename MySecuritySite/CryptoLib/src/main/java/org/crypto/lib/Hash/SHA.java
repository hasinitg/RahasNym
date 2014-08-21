package org.crypto.lib.Hash;

import org.crypto.lib.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/17/14
 * Time: 11:10 AM
 */

/**
 * This class exposes methods to obtain the hash of a given string using different hash functions.
 */
public class SHA {
    public static byte[] SHA256(String message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance(Constants.SHA256);
        byte[] messageBytes = message.getBytes();
        byte[] digest = sha256.digest(messageBytes);
        return digest;
    }
}
