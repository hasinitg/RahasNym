package org.crypto.lib.tests.algorithms;

import org.crypto.lib.algorithms.EuclideanAlgorithm;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/22/14
 * Time: 1:50 PM
 */
public class TestEuclidean {
    public static void main(String[] args) throws CryptoAlgorithmException {
        int gcd = EuclideanAlgorithm.getGCDthruRecursiveEuclid(12, 18);
        //should be 6
        System.out.println(gcd);

        int gcd1 = EuclideanAlgorithm.getGCDthruRecursiveEuclid(4864, 3458);
        //should be 38
        System.out.println(gcd1);

        int gcd2 = EuclideanAlgorithm.getGCDThruNonRecursiveEuclid(12, 18);
        System.out.println(gcd2);

        int gcd3 = EuclideanAlgorithm.getGCDThruNonRecursiveEuclid(4864, 3458);
        System.out.println(gcd3);
    }
}
