package org.rahasnym.api;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/30/14
 * Time: 9:00 PM
 */
public class CryptoTest {

    private static String absoluteKeyStoreFilePath = "/home/hasini/Hasini/Experimenting/RahasNym/SPClient/src/main/resources/clientTrustStore.jks";
    private static String trustStore = "src/main/resources/clientTrustStore.jks";
    private static String trustStorePassword = "rahasnymClient";
    private static String publicKeyAlias = "rahasnymSPCert";
    private static String encryptionAlgorithm = "RSA";

    private static String absoluteSPKeyStoreFilePath = "/home/hasini/Hasini/Experimenting/RahasNym/ServiceProvider/src/main/resources/spKeyStore.jks";
    private static String keyStore = "/src/main/resources/spKeyStore.jks";
    private static String keyStorePassword = "rahasnymSPKS";
    private static String privateKeyAlias = "rahasnymSPCert";

    private static String plaintext = "text to be encrypted.";

    public static void main(String[] args) {
        try {

            /**********************At Sender****************************************/

            //read the trust store store file and load it into the trust store object.
            KeyStore trustStore = KeyStore.getInstance("JKS");
            char[] storePass = trustStorePassword.toCharArray();
            FileInputStream trustStoreFile = new FileInputStream(absoluteKeyStoreFilePath);
            trustStore.load(trustStoreFile, storePass);

            //read the public certificate of the receiver from the trust store.
            Certificate publicCert = trustStore.getCertificate(publicKeyAlias);
            PublicKey rPublicKey = publicCert.getPublicKey();

            //initialize the cipher to do the encryption.
            Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, rPublicKey);

            //perform encryption
            byte[] cipherText = cipher.doFinal(plaintext.getBytes());

            //check the length of the cipher text in bytes.
            System.out.println("" + cipherText.length);

            //convert cipher text in to string by encoding it.
            String cipherTextString = Base64.encodeBytes(cipherText);

            //print cipher text as string and its encoded length.
            System.out.println("" + cipherTextString.length());
            System.out.println(cipherTextString);

            /******************At Receiver*************************************/

            //decode the cipher text.
            byte[] cipherTextAtReceiver = Base64.decode(cipherTextString);

            //read the key store file and load it into the key store object.
            KeyStore rKeyStore = KeyStore.getInstance("JKS");
            char[] rStorePass = keyStorePassword.toCharArray();
            FileInputStream rKeyStoreFile = new FileInputStream(absoluteSPKeyStoreFilePath);
            rKeyStore.load(rKeyStoreFile, rStorePass);

            //retrieve the private key
            PrivateKey privateKey = (PrivateKey) rKeyStore.getKey(privateKeyAlias, rStorePass);
            Cipher decryptingCipher = Cipher.getInstance(encryptionAlgorithm);
            decryptingCipher.init(Cipher.DECRYPT_MODE, privateKey);

            //perform decryption
            byte[] plainText = decryptingCipher.doFinal(cipherTextAtReceiver);
            String recoveredPlainText = new String(plainText);
            System.out.println(recoveredPlainText);

        } catch (KeyStoreException e) {
            String error = "Error in initializing the key store.";
            System.out.println(error);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            String error = "Unable to find the key store file.";
            System.out.println(error);
            e.printStackTrace();
        } catch (CertificateException e) {
            String error = "Error in loading the key store.";
            System.out.println(error);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            String error = "Error in loading the key store.";
            System.out.println(error);
            e.printStackTrace();
        } catch (IOException e) {
            String error = "Error in loading the key store.";
            System.out.println(error);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            String error = "Error in initializing the cipher.";
            System.out.println(error);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            String error = "Error in initializing the cipher. Invalid key.";
            System.out.println(error);
            e.printStackTrace();
        } catch (BadPaddingException e) {
            String error = "Error in encrypting.";
            System.out.println(error);
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            String error = "Error in encrypting.";
            System.out.println(error);
            e.printStackTrace();
        } /*catch (Base64DecodingException e) {
            String error = "Error in decoding the cipher text encoded string.";
            System.out.println(error);
            e.printStackTrace();
        }*/ catch (UnrecoverableKeyException e) {
            String error = "Error in reading the private key.";
            System.out.println(error);
            e.printStackTrace();
        }
    }
}
