package org.crypto.lib.commitments.pedersen;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/26/14
 * Time: 10:06 AM
 */
public class PedersenCommitment {

    private BigInteger commitment;

    private PedersenPublicParams publicParams;

    /**Although we include these two properties in this object and expose getters and setters;
     * user should be careful not to store the values in an object that could be accessed by
     * another party.*/

    /* first value hidden in the commitment*/
    private BigInteger x;

    /*second value hidden in the commitment*/
    private BigInteger r;

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getR() {
        return r;
    }

    public void setR(BigInteger r) {
        this.r = r;
    }

    public BigInteger getCommitment() {
        return commitment;
    }

    public void setCommitment(BigInteger commitment) {
        this.commitment = commitment;
    }

    public PedersenPublicParams getPublicParams() {
        return publicParams;
    }

    public void setPublicParams(PedersenPublicParams publicParams) {
        this.publicParams = publicParams;
    }
}
