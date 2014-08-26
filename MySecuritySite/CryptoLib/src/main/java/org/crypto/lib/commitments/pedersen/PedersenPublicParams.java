package org.crypto.lib.commitments.pedersen;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/26/14
 * Time: 10:06 AM
 */
public class PedersenPublicParams {
    private BigInteger p;
    private BigInteger q;
    private BigInteger g;
    private BigInteger h;

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getH() {
        return h;
    }

    public void setH(BigInteger h) {
        this.h = h;
    }
}
