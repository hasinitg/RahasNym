package org.crypto.lib.util;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/25/14
 * Time: 3:39 PM
 */

/**
 * This is a util class that provides method to manipulate integers, as required when implementing
 * certain cryptographic operations/protocols and algorithms.
 */
public class IntegerManipulation {

    public static String getBinaryRepresentation(int x){
        return Integer.toBinaryString(x);
    }

    public static String getRepresentationInDiffBase(int x, int base){
        return Integer.toString(x, base);
    }

    public static int[] getBitsInArrayRightToLeft(int x){
        return null;
    }

    public static int[] getBitsInArrayLeftToRight(int x){
        return null;
    }

}
