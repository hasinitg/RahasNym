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
        //TODO:check if taking only the positive value, affects the collision-resistance of hash output
        return new BigInteger(1, valueForCommitment);
    }

    public static BigInteger getCommittableThruPBKDF(String password, int bitLengthOfKey, int iterationCount)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        //create salt for password based key derivation
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        byte[] derivedSecret = PBKDF.deriveKeyWithPBKDF5(password, salt, iterationCount, bitLengthOfKey);
        //convert the derived secret into a big integer in order to create the commitment.
        return new BigInteger(1, derivedSecret);
    }
}
