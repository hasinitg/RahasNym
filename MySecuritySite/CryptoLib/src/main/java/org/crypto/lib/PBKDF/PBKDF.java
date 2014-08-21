package org.crypto.lib.PBKDF;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/17/14
 * Time: 3:02 PM
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crypto.lib.Constants;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * This class exposes functions to generate keys based on a given password, using different pseudo
 * random functions. Currently, it supports PBKDF#5 which looks at only the low order 8 bits of
 * each character in the password.
 * TODO: PBKDF#12 which looks at all 16 bits of each character of the password.
 */
public class PBKDF {

    private static Log logger = LogFactory.getLog(PBKDF.class);

    /**
     * This method returns the derived key in bytes, based on the given password.
     *
     * @param password - based on which the key should be derived.
     * @param salt - used to avoid dictionary attacks. (recommended length: 64 bits
     *               as per http://www.ietf.org/rfc/rfc2898.txt)
     * @param iterationCount - indicates how many times to iterate the underlying function by
                               which the key is derived. minimum of 1000 is recommended as per:
                               http://www.ietf.org/rfc/rfc2898.txt. This will increase the cost of
                               exhaustive search for passwords significantly, without a noticeable
                               impact in the cost of deriving individual keys
     * @param keyLength - length of the key to be derived, in number of bytes.
     * @return - the derived key in the form of a byte array.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] deriveKeyWithPBKDF5(String password, byte[] salt, int iterationCount, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(Constants.PBKDF5);
        char[] pass = password.toCharArray();
        PBEKeySpec keySpec = new PBEKeySpec(pass, salt, iterationCount, keyLength);
        Key key = secretKeyFactory.generateSecret(keySpec);
        /*uncomment following if you want to look at the derived key.*/
        //logger.debug(Arrays.toString(key.getEncoded()));
        return key.getEncoded();
    }
}
