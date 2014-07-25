package org.crypto.lib.algorithms;

import org.crypto.lib.exceptions.CryptoAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/22/14
 * Time: 1:41 PM
 */

/**
 * Efficient algorithm for finding GCD.
 */
public class EuclideanAlgorithm {

    public static int getGCDthruRecursiveEuclid(int a, int b) throws CryptoAlgorithmException {
        //check validation of inputs: a, b > 0, a > b
        if (((a < 0) || (b < 0)) && (a < b)) {
            throw new CryptoAlgorithmException("Inputs provided for the RecursiveEuclid algorithm is incorrect." +
                    "Both inputs must be positive and first input must be greater than second input.");
        }
        if (b == 0) {
            return a;
        } else {
            return getGCDthruRecursiveEuclid(b, a % b);
        }
    }

    public static int getGCDThruNonRecursiveEuclid(int a, int b) throws CryptoAlgorithmException {
        //check validation of inputs: a, b > 0, a > b
        if (((a < 0) || (b < 0)) && (a < b)) {
            throw new CryptoAlgorithmException("Inputs provided for the RecursiveEuclid algorithm is incorrect." +
                    "Both inputs must be positive and first input must be greater than second input.");
        }
        while (b > 0) {
            int r = a % b ;
            a = b;
            b = r;
        }
        return a;
    }
}
