package org.crypto.lib.zero.knowledge.proof;

import org.crypto.lib.Hash.SHA;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
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
    int bitLengthOfElementInZq;

    public ZKPPedersenCommitment(PedersenPublicParams publicParameters) throws CryptoAlgorithmException {
        this.publicParams = publicParameters;
        pedersenCommitmentFactory.initialize(publicParams);
        bitLengthOfElementInZq = this.publicParams.getQ().bitLength() - 1;
    }

    @Override
    public PedersenCommitment createHelperProblem(PedersenCommitment originalProblem) {

        //create two dummy values in Zq for the two secrets
        BigInteger x = new BigInteger(bitLengthOfElementInZq, new SecureRandom());
        BigInteger r = new BigInteger(bitLengthOfElementInZq, new SecureRandom());
        PedersenCommitment helperCommitment = pedersenCommitmentFactory.createCommitment(x, r);
        //set the two values in the commitment so that the proof creation can access them
        helperCommitment.setX(x);
        helperCommitment.setR(r);
        return helperCommitment;
    }

    @Override
    public List<PedersenCommitment> createHelperProblems(PedersenCommitment originalProblem) {
        List<PedersenCommitment> helperProblems = new ArrayList<>();
        /*according to our algorithm, we restrict to three helper problems, because we think it is secure enough
        to prevent prover from cheating.*/
        for (int i = 0; i < 3; i++) {

            PedersenCommitment helperProblem = createHelperProblem(null);
            helperProblems.add(helperProblem);
        }
        return helperProblems;
    }

    @Override
    public BigInteger createChallengeForInteractiveZKP() {
        //TODO: check if the length of the challenge is optimum
        return new BigInteger(bitLengthOfElementInZq, new SecureRandom());
    }

    @Override
    public List<BigInteger> createChallengeForNonInteractiveZKP(
            PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems) throws NoSuchAlgorithmException {
        /*according to our algorithm, we restrict to three challenges obtained from the hash of original problem and
        three helper problems, because we think it is secure enough to prevent prover from cheating.*/

        //convert original problem and three helper problems into byte, concatenate them and take the hash
        byte[] originalCommitmentBytes = originalProblem.getCommitment().toByteArray();

        int concatenatedCommitmentsByteLength = 0;
        concatenatedCommitmentsByteLength += originalCommitmentBytes.length;

        List<byte[]> helperCommitmentsBytes = new ArrayList<>();
        for (PedersenCommitment helperProblem : helperProblems) {
            byte[] helperCommitmentBytes = helperProblem.getCommitment().toByteArray();
            helperCommitmentsBytes.add(helperCommitmentBytes);
            concatenatedCommitmentsByteLength += helperCommitmentBytes.length;
        }

        byte[] concatenatedArray = new byte[concatenatedCommitmentsByteLength];
        int destPos = 0;
        System.arraycopy(originalCommitmentBytes, 0, concatenatedArray, destPos, originalCommitmentBytes.length);
        destPos += originalCommitmentBytes.length;
        for (byte[] helperCommitmentBytes : helperCommitmentsBytes) {
            int length = helperCommitmentBytes.length;
            System.arraycopy(helperCommitmentBytes, 0, concatenatedArray, destPos, length);
            destPos += length;

        }
        byte[] hash = SHA.SHA256(concatenatedArray);

        //now get the initial 160*3 bytes and create three challenges; such that each of them are in Zq.
        List<BigInteger> challenges = new ArrayList<>();
        int lengthOfChallengeInBytes = bitLengthOfElementInZq/8;
        int challengeDestPos = 0;
        byte[] challengeBytes = new byte[lengthOfChallengeInBytes];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(hash, 0, challengeBytes, challengeDestPos, lengthOfChallengeInBytes);
            BigInteger challengeBigInt = new BigInteger(challengeBytes);
            challenges.add(challengeBigInt);
            challengeDestPos += lengthOfChallengeInBytes;
        }
        return challenges;
    }

    @Override
    public List<BigInteger> createChallengeForNonInteractiveZKPWithSignature(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems, byte[] message) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PedersenCommitmentProof> createProofForNonInteractiveZKP(PedersenCommitment originalProblem,
                                                                         List<PedersenCommitment> helperProblems,
                                                                         List<BigInteger> challenges) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PedersenCommitmentProof createProofForInteractiveZKP(PedersenCommitment originalProblem,
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
    public boolean verifyInteractiveZKP(PedersenCommitment originalProblem, PedersenCommitment helperProblem,
                                        BigInteger challenge, PedersenCommitmentProof proof) {
        //check if dc^e = g^uh^v
        BigInteger c = originalProblem.getCommitment();
        BigInteger d = helperProblem.getCommitment();
        BigInteger LHS = (d.multiply(c.modPow(challenge, publicParams.getP()))).mod(publicParams.getP());
        PedersenCommitment RHS = pedersenCommitmentFactory.createCommitment(publicParams, proof.getU(), proof.getV());
        return (LHS.compareTo(RHS.getCommitment()) == 0);
    }

    @Override
    public boolean verifyNonInteractiveZKP(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems,
                                           List<BigInteger> challenges, List<PedersenCommitmentProof> proofs) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean verifyNonInteractiveZKPWithSignature(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblem, byte[] message, List<BigInteger> challenges, List<PedersenCommitmentProof> proofs) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}