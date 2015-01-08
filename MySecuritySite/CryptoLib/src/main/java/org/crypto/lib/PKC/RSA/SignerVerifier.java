package org.crypto.lib.PKC.RSA;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/4/15
 * Time: 4:57 PM
 */

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.util.Base64;

import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;

/**
 * This performs creating and verification of digital signature based on RSA PKC.
 */
public class SignerVerifier {

    /**
     * Sign the message provided as String and return the Base64 encoded signature.
     *
     * @param message
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public String signMessageAsString(String message, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] signedContent = signContentAsBytes(message.getBytes(), privateKey);
        return Base64.encodeBytes(signedContent);
    }

    /**
     * Sign the message provided as String and return the signature as a byte array.
     *
     * @param message
     * @param privateKey
     * @return
     */
    public byte[] signMessageAsBytes(String message, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        return signContentAsBytes(message.getBytes(), privateKey);
    }

    /**
     * Sign the content provided as a byte array and return the signature as String.
     *
     * @param content
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public String signContentAsString(byte[] content, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] signedContent = signContentAsBytes(content, privateKey);
        return Base64.encodeBytes(signedContent);
    }

    /**
     * Sign the content provided as a byte array and return the signature as a String.
     *
     * @param content
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public byte[] signContentAsBytes(byte[] content, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(CryptoLibConstants.RSA_SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content);
        return signature.sign();
    }

    /**
     * Verify the Base64 encoded signature against String content.
     *
     * @param message
     * @param signature
     * @param publicCert
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public boolean verifySignature(String message, String signature, Certificate publicCert) throws NoSuchAlgorithmException,
            InvalidKeyException, IOException, SignatureException {
        return verifySignature(message.getBytes(), Base64.decode(signature), publicCert);
    }

    public boolean verifySignature(String message, byte[] signature, Certificate publicCert) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        return verifySignature(message.getBytes(), signature, publicCert);
    }

    public boolean verifySignature(byte[] message, String signature, Certificate publicCert) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return verifySignature(message, Base64.decode(signature), publicCert);
    }

    public boolean verifySignature(byte[] message, byte[] signature, Certificate publicCert) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature signatureInstance = Signature.getInstance(CryptoLibConstants.RSA_SIGNATURE_ALGORITHM);
        signatureInstance.initVerify(publicCert);
        signatureInstance.update(message);
        return signatureInstance.verify(signature);
    }
}
