package org.crypto.lib.commitments;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 7/25/14
 * Time: 3:34 PM
 */

/**
 * This class encapsulates the logic related to initializing, creating and verifying
 * of the pedersen commitment scheme which is defined in:
 * Pedersen, T.P.: Non-interactive and information-theoretic secure verifiable secret
 * sharing. In: Proceedings of Advances in Cryptology - CRYPTO’91 (1992).
 */
public class PedersenCommitment {

    private Log log = LogFactory.getLog(PedersenCommitment.class);

    private PublicParams publicParams = new PublicParams();

    private BigInteger a;

    /**
     * This method initializes the public parameters: p q. g. h and the secret 'a' for the
     * pedersen commitment using the DSA KeyPairGenerator provided in Java, which
     * uses same set of parameters as in pedersen commitment scheme.
     *
     * @return Initialized public parameters involved in the commitment scheme: p, q, g, h
     * @throws NoSuchAlgorithmException
     */
    public PublicParams initialize() throws NoSuchAlgorithmException, CryptoAlgorithmException {

        Boolean parametersValid = false;
        BigInteger p = BigInteger.ONE, q = BigInteger.ONE, g = BigInteger.ONE, h = BigInteger.ONE;
        //check the validity of parameters
        while (!parametersValid) {

            BigInteger[] parametersPQG = generatePQG();
            p = parametersPQG[0];
            q = parametersPQG[1];
            g = parametersPQG[2];

            if (log.isDebugEnabled()) {
                String message = "Bit length of q: " + q.bitLength();
                log.debug(message);
            }

            // check if p mod q = 1?
            BigInteger z = p.mod(q);
            if (log.isDebugEnabled()) {
                String message = "p mod q = " + z.toString();
                log.debug(message);
            }
            if (z.compareTo(BigInteger.ONE) == 0) {
                parametersValid = true;
            }

            // check if g^q mod p = 1?
            z = g.modPow(q, p);
            if (log.isDebugEnabled()) {
                String message = "g^q mod p = " + z.toString();
                log.debug(message);
            }
            if (z.compareTo(BigInteger.ONE) == 0) {
                parametersValid = true;
            }
        }

        publicParams.setP(p);
        publicParams.setQ(q);
        publicParams.setG(g);

        Boolean aValid = false;
        while (!aValid) {
            // find an integer a in Zq
            a = new BigInteger(q.bitLength(), new SecureRandom());
            if (a.equals(a.mod(q))) {
                aValid = true;
                if (log.isDebugEnabled()) {
                    log.debug("a = " + a.toString() + " calculated okay.");
                } else {
                    log.debug("a = " + a.toString() + " calculated incorrect.");
                }
            }
        }

        // Calculate element h of Gq
        h = g.modPow(a, p);
        // check if h^q mod p = 1?
        BigInteger z = h.modPow(q, p);
        if (z.compareTo(BigInteger.ONE) != 0) {

            log.error("h^q mod p = " + z.toString());
            throw new CryptoAlgorithmException("Generated parameters of Pedersen Commitment Scheme are incorrect.");
        }
        publicParams.setH(h);
        //return finalParams;
        return publicParams;
    }

    /**
     *
     * @param publicParams
     */
    public void initialize(PublicParams publicParams){

    }

    /**
     * This method computes the pedersen commitment, given the public parameters: p, q, g, h, the secret: x and
     * the random value r.
     *
     * @param publicParams
     * @param secret
     * @param random
     * @return
     */
    public BigInteger createCommitment(PublicParams publicParams, BigInteger secret, BigInteger random) {
        secret = secret.mod(publicParams.getQ());
        random = random.mod(publicParams.getQ());
        BigInteger intermediate1 = publicParams.getG().modPow(secret, publicParams.getP());
        BigInteger intermediate2 = publicParams.getH().modPow(random, publicParams.getP());
        //create the commitment: C = g^x.h^y (mod p)
        BigInteger commitment = intermediate1.multiply(intermediate2);
        if (log.isDebugEnabled()) {
            log.debug("Commitment string: " + commitment.toString());
            log.debug("Commitment bit length: " + commitment.bitLength());
        }
        return commitment;
    }

    /**
     * This method computes the pedersen commitment, given the secret: x and the random value r.
     * The public parameters are taken from those created in the initialize method.
     * @param secret
     * @param random
     * @return
     */
    public BigInteger createCommitment(BigInteger secret, BigInteger random) {
        secret = secret.mod(publicParams.getQ());
        random = random.mod(publicParams.getQ());
        BigInteger intermediate1 = publicParams.getG().modPow(secret, publicParams.getP());
        BigInteger intermediate2 = publicParams.getH().modPow(random, publicParams.getP());
        //create the commitment: C = g^x.h^y (mod p)
        BigInteger commitment = intermediate1.multiply(intermediate2);
        if (log.isDebugEnabled()) {
            log.debug("Commitment string: " + commitment.toString());
            log.debug("Commitment bit length: " + commitment.bitLength());
        }
        return commitment;
    }

    /**
     * Helper method that encapsulate the logic of generating the parameters: p.q.g
     *
     * @return
     */
    private BigInteger[] generatePQG() throws NoSuchAlgorithmException {

        BigInteger[] parameters = new BigInteger[3];

        // generate a DSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        //to provide randomness to the generated key
        SecureRandom random = new SecureRandom();

        keyGen.initialize(1024, random);
        KeyPair pair = keyGen.generateKeyPair();

        // cast as DSAKey in order to access G, P, Q directly
        BigInteger g, p, q;
        DSAKey privKey = (DSAKey) pair.getPrivate();
        DSAParams params = privKey.getParams();
        g = (BigInteger) params.getG();
        if (log.isDebugEnabled()) {
            String debugMsg = "generator g = " + g.toString();
            log.debug(debugMsg);
        }
        p = (BigInteger) params.getP();
        if (log.isDebugEnabled()) {
            String debugMsg = "prime p = " + p.toString();
            log.debug(debugMsg);
        }
        q = (BigInteger) params.getQ();
        if (log.isDebugEnabled()) {
            String debugMsg = "order q of group G(q) = " + q.toString();
            log.debug(debugMsg);
        }
        parameters[0] = p;
        parameters[1] = q;
        parameters[2] = g;

        return parameters;
    }

    public class PublicParams {
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
}
