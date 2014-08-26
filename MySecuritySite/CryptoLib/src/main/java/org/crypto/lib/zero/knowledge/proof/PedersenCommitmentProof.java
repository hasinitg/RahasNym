package org.crypto.lib.zero.knowledge.proof;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/25/14
 * Time: 9:55 AM
 */
public class PedersenCommitmentProof {

    private BigInteger u;
    private BigInteger v;

    public BigInteger getU() {
        return u;
    }

    public void setU(BigInteger u) {
        this.u = u;
    }

    public BigInteger getV() {
        return v;
    }

    public void setV(BigInteger v) {
        this.v = v;
    }

}
