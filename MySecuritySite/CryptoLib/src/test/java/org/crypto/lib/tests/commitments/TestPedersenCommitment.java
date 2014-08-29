package org.crypto.lib.tests.commitments;

import junit.framework.Assert;
import org.crypto.lib.Hash.SHA;
import org.crypto.lib.PBKDF.PBKDF;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.junit.Test;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/29/14
 * Time: 10:54 AM
 */
public class TestPedersenCommitment {

    @Test
    public void testInitialization() {

        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        PedersenPublicParams params = null;
        try {
            params = pedersenCommitmentFactory.initialize();
        } catch (NoSuchAlgorithmException e) {
            Assert.fail(e.getMessage());
        } catch (CryptoAlgorithmException e) {
            Assert.fail(e.getMessage());
        }
        if ((params.getP() == null) || (params.getQ() == null) || (params.getG() == null) || (params.getH() == null)) {
            Assert.fail("Public parameters are not initialized properly.");
        }
        System.out.println("P: " + params.getP());
        System.out.println("Q: " + params.getQ());
        System.out.println("G: " + params.getG());
        System.out.println("H: " + params.getH());
        System.out.println();
    }


    @Test
    public void testCommitmentCreation() {
        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        PedersenPublicParams params = null;
        try {
            params = pedersenCommitmentFactory.initialize();
        } catch (NoSuchAlgorithmException e) {
            Assert.fail(e.getMessage());
        } catch (CryptoAlgorithmException e) {
            Assert.fail(e.getMessage());
        }
        //create dummy x and r for the commitment:
        BigInteger x = new BigInteger(params.getQ().bitLength() - 1, new SecureRandom());
        BigInteger r = new BigInteger(params.getQ().bitLength() - 1, new SecureRandom());
        PedersenCommitment commitment = pedersenCommitmentFactory.createCommitment(x, r);
        BigInteger commitmentString = commitment.getCommitment();
        //test for the commitment bit length
        Assert.assertTrue((commitmentString.bitLength() < params.getP().bitLength()) ||
                (commitmentString.bitLength() == params.getP().bitLength()));
        System.out.println("Commitment from random values: " + commitmentString);
        System.out.println();

    }

    @Test
    public void testCommitmentVerification() {
        /**
         * This test shows how to hide a real value in a pedersen commitment along with
         * a user-provided password.
         */
        String email = "hasi7786@gmail.com";
        String password = "xxyyzz";

        try {
            /**
             * We need to convert email and the secret into the domain of Zq where q is 160 bits.
             * In order to do that, we use a hash function to derive a value in that domain corresponding to
             * the value of email and we use password based key derivation to derive a secret to be used in
             * the pedersen commitment, based on the user-provided password.
             * The latter is specifically useful when we use the same password of the user to derive multiple keys.
             */
            byte[] emailHash = SHA.SHA256(email);
            //extract the first 160 bits.
            byte[] emailForCommitment = new byte[19];
            for (int i = 0; i < 19; i++) {
                emailForCommitment[i] = emailHash[i];
            }
            //convert it to a big integer in order to create the commitment.
            BigInteger emailBI = new BigInteger(1, emailForCommitment);
            System.out.println("Committable value from email: " + emailBI);
            System.out.println("Length of the committable value:" + emailBI.bitLength());
            byte[] salt = new byte[8];
            new SecureRandom().nextBytes(salt);
            byte[] derivedSecret = PBKDF.deriveKeyWithPBKDF5(password, salt, 1000, 159);
            //convert the derived secret into a big integer in order to create the commitment.
            BigInteger secretBI = new BigInteger(derivedSecret);
            System.out.println("Committable value from derived secret: " + secretBI);
            System.out.println("Length of the committable secret: " + derivedSecret.length);
            //now create the commitment with the email and the secret C = g^xh^r
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            pedersenCommitmentFactory.initialize();
            PedersenCommitment commitment = pedersenCommitmentFactory.createCommitment(emailBI, secretBI);
            BigInteger commitmentString = commitment.getCommitment();
            System.out.println("Email commitment: " + commitmentString);
            System.out.println("Length of the email commitment: " + commitmentString.bitLength());
            System.out.println();
            Assert.assertEquals(true, pedersenCommitmentFactory.openCommitment(commitmentString, emailBI, secretBI));

        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Hash algorithm not found.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Key Specification for deriving the secret based on the user-password has an issue.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Error in initializing the pedersen commitment.");
        }

    }

}
