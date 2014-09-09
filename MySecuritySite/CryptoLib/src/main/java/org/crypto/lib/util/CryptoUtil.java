package org.crypto.lib.util;

import org.crypto.lib.Hash.SHA;
import org.crypto.lib.PBKDF.PBKDF;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/25/14
 * Time: 12:21 PM
 */
public class CryptoUtil {

    public static BigInteger getCommittableThruHash(String value, int bitLength) throws NoSuchAlgorithmException {
        byte[] valueHash = SHA.SHA256(value);
        //extract the first 160 bits.
        byte[] valueForCommitment = new byte[bitLength / 8];
        for (int i = 0; i < bitLength / 8; i++) {
            valueForCommitment[i] = valueHash[i];
        }
        //convert it to a big integer in order to create the commitment.
        //TODO:check if taking only the positive value, affects the collision-resistancy of hash output
        return new BigInteger(1, valueForCommitment);
    }

    public static BigInteger getCommittableThruPBKDF(String password, byte[] salt, int bitLengthOfKey, int iterationCount)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        byte[] derivedSecret = PBKDF.deriveKeyWithPBKDF5(password, salt, bitLengthOfKey, iterationCount);
        //convert the derived secret into a big integer in order to create the commitment.
        //TODO:check if taking only the positive value, affects the collision-resistancy of hash output
        return new BigInteger(1, derivedSecret);
    }

    public static byte[] generateSalt(int byteLength) {
        byte[] salt = new byte[byteLength];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
