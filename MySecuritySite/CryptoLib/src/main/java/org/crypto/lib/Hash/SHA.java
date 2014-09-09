package org.crypto.lib.Hash;

import org.crypto.lib.CryptoLibConstants;

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
        MessageDigest sha256 = MessageDigest.getInstance(CryptoLibConstants.SHA256);
        byte[] messageBytes = message.getBytes();
        byte[] digest = sha256.digest(messageBytes);
        return digest;

    }

    public static byte[] SHA256(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance(CryptoLibConstants.SHA256);
        byte[] digest = sha256.digest(message);
        return digest;
    }

    public static byte[] SHA512(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest sha512 = MessageDigest.getInstance(CryptoLibConstants.SHA512);
        byte[] digest = sha512.digest(message);
        return digest;
    }
}
