package org.crypto.lib.tests.algorithms;

import org.crypto.lib.algorithms.ExtendedEuclidAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/23/14
 * Time: 4:52 AM
 */
public class TestExtendedEuclid {
    public static void main(String[] args) {
        /*ExtendedEuclidAlgorithm.Result result = ExtendedEuclidAlgorithm.getExtendedRecursiveGCD(9, 4);
        System.out.println("d:" + result.getD());
        System.out.println("x:" + result.getX());
        System.out.println("y:" + result.getY());

        int x = (int) Math.floor(9/4);
        System.out.println(x);

        ExtendedEuclidAlgorithm.Result result2 = ExtendedEuclidAlgorithm.getExtendedNonRecursiveGCD(9, 4);
        System.out.println("d:" + result2.getD());
        System.out.println("x:" + result2.getX());
        System.out.println("y:" + result2.getY());

        //following example from:http://www.math.utah.edu/~fguevara/ACCESS2013/Euclid.pdf x = -22, y=147
        ExtendedEuclidAlgorithm.Result result3 = ExtendedEuclidAlgorithm.getExtendedRecursiveGCD(42823, 6409);
        System.out.println("d:" + result3.getD());
        System.out.println("x:" + result3.getX());
        System.out.println("y:" + result3.getY());

        ExtendedEuclidAlgorithm.Result result4 = ExtendedEuclidAlgorithm.getExtendedRecursiveGCD(11, 8);
        System.out.println("d:" + result4.getD());
        System.out.println("x:" + result4.getX());
        System.out.println("y:" + result4.getY());*/

        ExtendedEuclidAlgorithm.Result result5 = ExtendedEuclidAlgorithm.getExtendedRecursiveGCD(9, 1);
        System.out.println("d:" + result5.getD());
        System.out.println("x:" + result5.getX());
        System.out.println("y:" + result5.getY());
    }
}
