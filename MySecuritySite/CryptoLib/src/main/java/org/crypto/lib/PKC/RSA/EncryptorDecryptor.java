package org.crypto.lib.PKC.RSA;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/4/15
 * Time: 10:36 AM
 */

/**
 * This performs asymmetric encryption/decryption based on RSA algorithm.
 */
public class EncryptorDecryptor {

    /**
     * Encrypt the content provided as String and return the Base64 encoded cipher text.
     * @param plainText
     * @param publicCert
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encryptStringAsString(String plainText, Certificate publicCert) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return encryptBytesAsString(plainText.getBytes(), publicCert);
    }

    /**
     * Encrypt the content provided as a byte array and return the Base64 encoded cipher text.
     * @param plainText
     * @param publicCert
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encryptBytesAsString(byte[] plainText, Certificate publicCert) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //read the public key from the certificate.
        PublicKey publicKey = publicCert.getPublicKey();

        //initialize the cipher to do the PKC.
        Cipher cipher = Cipher.getInstance(CryptoLibConstants.RSA_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //perform PKC
        byte[] cipherText = cipher.doFinal(plainText);

        //convert cipher text in to string by encoding it.
        return Base64.encodeBytes(cipherText);
    }

    /**
     * Encrypt the content provided as a String and return the cipher text as a byte array.
     * @param plainText
     * @param publicCert
     * @return
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encryptStringAsBytes(String plainText, Certificate publicCert) throws IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return encryptBytesAsBytes(plainText.getBytes(), publicCert);
    }

    /**
     * Encrypt the content provided as a byte array and return the cipher text as a byte array.
     * @param plainText
     * @param publicCert
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encryptBytesAsBytes(byte[] plainText, Certificate publicCert) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //read the public key from the certificate.
        PublicKey publicKey = publicCert.getPublicKey();

        //initialize the cipher to do the PKC.
        Cipher cipher = Cipher.getInstance(CryptoLibConstants.RSA_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //perform PKC
        return cipher.doFinal(plainText);

    }

    /**
     * Decrypt the Base64 encoded cipher text and return the plain text as string.
     * @param cipherText
     * @param privateKey
     * @return
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decryptStringAsString(String cipherText, PrivateKey privateKey) throws IOException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //decode the cipher text.
        byte[] cipherTextBytes = Base64.decode(cipherText);
        return new String(decryptBytesAsBytes(cipherTextBytes, privateKey));
    }

    /**
     * Decrypt the cipher text provided as a byte array and return the plain text as string.
     * @param cipherText
     * @param privateKey
     * @return
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String decryptBytesAsString(byte[] cipherText, PrivateKey privateKey) throws IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return new String(decryptBytesAsBytes(cipherText, privateKey));
    }

    /**
     * Decrypt the Base64 encoded cipher text and return the plain text as bytes.
     * @param cipherText
     * @param privateKey
     * @return
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decryptStringAsBytes(String cipherText, PrivateKey privateKey) throws IOException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        //decode the cipher text.
        byte[] cipherTextBytes = Base64.decode(cipherText);
        return decryptBytesAsBytes(cipherTextBytes, privateKey);
    }

    /**
     * Decrypt the cipher text provided as byte array and return the plain text as byte array.
     * @param cipherText
     * @param privateKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decryptBytesAsBytes(byte[] cipherText, PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //retrieve the private key
        Cipher decryptingCipher = Cipher.getInstance(CryptoLibConstants.RSA_ENCRYPTION_ALGORITHM);
        decryptingCipher.init(Cipher.DECRYPT_MODE, privateKey);

        //perform decryption
        return decryptingCipher.doFinal(cipherText);
    }
}
