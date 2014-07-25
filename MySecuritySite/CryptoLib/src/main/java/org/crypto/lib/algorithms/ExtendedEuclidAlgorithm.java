package org.crypto.lib.algorithms;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/23/14
 * Time: 4:23 AM
 */
public class ExtendedEuclidAlgorithm {

    public static Result getExtendedRecursiveGCD(int a, int b) {

        //TODO: add  check validation
        if (b == 0) {
            Result result = new Result();
            result.d = a;
            result.x = 1;
            result.y = 0;

            return result;

        } else {
            Result intermediateResult = getExtendedRecursiveGCD(b, a % b);
            Result result = new Result();
            result.d = intermediateResult.d;
            result.x = intermediateResult.y;
            int z = (int) Math.floor(a / b);
            result.y = intermediateResult.x - ((z * intermediateResult.y));
            return result;
        }
    }

    public static class Result {
        int d;
        int x;
        int y;

        public int getD() {
            return d;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static Result getExtendedNonRecursiveGCD(int a, int b) {
        //TODO:add check validation
        Result result = new Result();
        if (b == 0) {
            result.d = a;
            result.x = 1;
            result.y = 0;
            return result;
        }
        int x1, x2, y1, y2, q, r;
        x2 = 1;
        x1 = 0;
        y2 = 0;
        y1 = 1;
        while (b > 0) {
            q = (int) Math.floor(a / b);
            r = a - (q * b);
            result.x = x2 - (q * x1);
            result.y = y2 - (q * y1);
            a = b;
            b = r;
            x2 = x1;
            x1 = result.x;
            y2 = y1;
            y1 = result.y;
        }
        result.d = a;
        result.x = x2;
        result.y = y2;
        return result;

    }

    //TODO: add a method as print result so that non-cryptographer can also understand the result.
}
