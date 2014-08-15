package org.crypto.lib.tests.commitments;

import junit.framework.Assert;
import org.junit.Test;
import org.crypto.lib.commitments.PedersenCommitment;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/29/14
 * Time: 10:54 AM
 */
public class TestPedersenCommitment {

    @Test

    public static void main(String[] args) throws CryptoAlgorithmException, NoSuchAlgorithmException {
        PedersenCommitment pedersenCommitment = new PedersenCommitment();
        PedersenCommitment.PublicParams params = pedersenCommitment.initialize();
        System.out.println("P: " + params.getP());
        System.out.println("Q: " + params.getQ());
        System.out.println("G: " + params.getG());
        System.out.println("H: " + params.getH());

        //create sample x and r for the commitment:
        BigInteger x = new BigInteger(params.getQ().bitLength(), new SecureRandom());
        BigInteger r = new BigInteger(params.getQ().bitLength(), new SecureRandom());
        pedersenCommitment.createCommitment()

    }

}
