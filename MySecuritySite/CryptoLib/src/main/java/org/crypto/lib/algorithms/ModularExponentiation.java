package org.crypto.lib.algorithms;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/25/14
 * Time: 2:22 PM
 */
public class ModularExponentiation {

    /**
     * Compute modular exponentiation through repeated squaring and multiplication.
     *
     *
     * @param number
     * @param exponent
     * @param modulus
     * @return
     */
    /*public BigInteger computeModularExponentiation(int number, int exponent, int modulus) {
        //convert the exponent into binary
        String exponentInBinary = Integer.toBinaryString(exponent);
        //get the binary bits into an integer array
        int binaryLength = exponentInBinary.length();
        int[] binaryString = new int[binaryLength];

        for (int i = binaryLength; i >= 0; i--) {
            int index = 0;
            String bit = binaryString[index] = exponentInBinary.substring(i, i-1);
        }


    }*/
}
