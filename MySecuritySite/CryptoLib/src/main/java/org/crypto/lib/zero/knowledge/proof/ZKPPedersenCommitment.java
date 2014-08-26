package org.crypto.lib.zero.knowledge.proof;

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/25/14
 * Time: 9:44 AM
 */
public class ZKPPedersenCommitment implements ZKP<PedersenCommitment, PedersenCommitment, BigInteger, PedersenCommitmentProof> {

    PedersenPublicParams publicParams = null;
    PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
    public ZKPPedersenCommitment(PedersenPublicParams publicParameters) throws CryptoAlgorithmException {
        this.publicParams = publicParameters;
        pedersenCommitmentFactory.initialize(publicParams);

    }

    @Override
    public PedersenCommitment createHelperProblem(PedersenCommitment originalProblem) {

        //create two dummy values in Zq for the two secrets
        BigInteger x = new BigInteger(publicParams.getQ().bitLength() - 1, new SecureRandom());
        BigInteger r = new BigInteger(publicParams.getQ().bitLength() - 1, new SecureRandom());
        PedersenCommitment helperCommitment = pedersenCommitmentFactory.createCommitment(x, r);
        //set the two values in the commitment so that the proof creation can access them
        helperCommitment.setX(x);
        helperCommitment.setR(r);
        return helperCommitment;
    }

    @Override
    public List<PedersenCommitment> createHelperProblems(PedersenCommitment originalProblem) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigInteger createInteractiveChallenge() {
        //TODO: check if the length of the challenge is optimum
        return new BigInteger(publicParams.getQ().bitLength() - 1, new SecureRandom());
    }

    @Override
    public List<BigInteger> createNonInteractiveChallenge(byte[] hash) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PedersenCommitmentProof> createNonInteractiveProof(PedersenCommitment originalProblem,
                                                                   List<PedersenCommitment> helperProblems,
                                                                   List<BigInteger> challenges) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PedersenCommitmentProof createInteractiveProof(PedersenCommitment originalProblem,
                                                          PedersenCommitment helperProblem, BigInteger challenge) {
        BigInteger x = originalProblem.getX();
        BigInteger r = originalProblem.getR();

        BigInteger y = helperProblem.getX();
        BigInteger s = helperProblem.getR();

        BigInteger u = (y.add(challenge.multiply(x))).mod(publicParams.getQ());
        BigInteger v = (s.add(challenge.multiply(r))).mod(publicParams.getQ());

        PedersenCommitmentProof pedersenCommitmentProof = new PedersenCommitmentProof();
        pedersenCommitmentProof.setU(u);
        pedersenCommitmentProof.setV(v);

        return pedersenCommitmentProof;
    }

    @Override
    public boolean verifyInteractiveProof(PedersenCommitment originalProblem, PedersenCommitment helperProblem,
                                          BigInteger challenge, PedersenCommitmentProof proof) {
        //check if dc^e = g^uh^v
        BigInteger c = originalProblem.getCommitment();
        BigInteger d = helperProblem.getCommitment();
        BigInteger LHS = (d.multiply(c.modPow(challenge, publicParams.getP()))).mod(publicParams.getP());
        PedersenCommitment RHS = pedersenCommitmentFactory.createCommitment(publicParams, proof.getU(), proof.getV());
        return (LHS.compareTo(RHS.getCommitment()) == 0);
    }

    @Override
    public boolean verifyNonInteractiveProof(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems,
                                             List<BigInteger> challenges, List<PedersenCommitmentProof> proofs) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}