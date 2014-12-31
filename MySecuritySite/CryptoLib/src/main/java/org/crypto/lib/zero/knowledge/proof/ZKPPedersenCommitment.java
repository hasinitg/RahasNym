package org.crypto.lib.zero.knowledge.proof;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crypto.lib.Hash.SHA;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

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

    private static Log log = LogFactory.getLog(ZKPPedersenCommitment.class);
    PedersenPublicParams publicParams = null;
    PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
    int bitLengthOfElementInZq;
    int lengthOfChallengeInBytes;
    int numberOfProofs = 3;

    public ZKPPedersenCommitment(PedersenPublicParams publicParameters) throws CryptoAlgorithmException {
        this.publicParams = publicParameters;
        pedersenCommitmentFactory.initialize(publicParams);
        bitLengthOfElementInZq = this.publicParams.getQ().bitLength() - 1;
        lengthOfChallengeInBytes = bitLengthOfElementInZq / 8;

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
        for (int i = 0; i < numberOfProofs; i++) {

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

        /*according to our algorithm, we restrict to three challenges derived from the hash of the original problem and
        three helper problems concatenated together; because we think it is secure enough to prevent prover from cheating.*/

        //convert original problem and three helper problems into bytes, concatenate them and compute the hash.
        byte[] concatenatedArray = obtainConcatenatedOriginalNHelperCommitments(originalProblem, helperProblems);
        byte[] hash = SHA.SHA512(concatenatedArray);
        //derive the challenges from the hash
        return obtainChallengesFromHash(hash);
    }

    @Override
    public List<BigInteger> createChallengeForNonInteractiveZKPWithSignature(
            PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems, byte[] message) throws NoSuchAlgorithmException {

        //convert original problem and three helper problems into bytes, concatenate them and compute the hash.
        byte[] concatenatedArray = obtainConcatenatedOriginalNHelperCommitments(originalProblem, helperProblems);

        //compute the hash of the message and combine it with the above concatenated array.
        byte[] hashOfMessage = SHA.SHA512(message);

        int lengthOfCombinedString = concatenatedArray.length + hashOfMessage.length;
        byte[] combinedString = new byte[lengthOfCombinedString];
        System.arraycopy(concatenatedArray, 0, combinedString, 0, concatenatedArray.length);
        System.arraycopy(hashOfMessage, 0, combinedString, concatenatedArray.length, hashOfMessage.length);

        //compute the hash of the final array using SHA-512 (since it needs to be long enough to obtain three challenges.)
        byte[] finalHash = SHA.SHA512(combinedString);
        return obtainChallengesFromHash(finalHash);
    }

    @Override
    public List<PedersenCommitmentProof> createProofForNonInteractiveZKP(PedersenCommitment originalProblem,
                                                                         List<PedersenCommitment> helperProblems,
                                                                         List<BigInteger> challenges) {
        List<PedersenCommitmentProof> proofs = new ArrayList<>();
        for (int i = 0; i < numberOfProofs; i++) {
            PedersenCommitment helperCommitment = helperProblems.get(i);
            BigInteger challenge = challenges.get(i);
            PedersenCommitmentProof proof = createProofForInteractiveZKP(originalProblem, helperCommitment, challenge);
            proofs.add(proof);
        }
        return proofs;
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
    //todo: add meta data if there is any, to be included in the hash
    public boolean verifyNonInteractiveZKP(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems,
                                           List<BigInteger> challenges, List<PedersenCommitmentProof> proofs) throws NoSuchAlgorithmException {
        boolean verificationResult = false;
        //first, verify the challenges
        List<BigInteger> derivedChallenges = this.createChallengeForNonInteractiveZKP(originalProblem, helperProblems);
        for (int j = 0; j < numberOfProofs; j++) {
            BigInteger derivedChallenge = derivedChallenges.get(j);
            BigInteger originalChallenge = challenges.get(j);
            if (originalChallenge.compareTo(derivedChallenge) != 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Error in provided challenge: " + j + " for the non-interactive proof.");
                }
                return false;
            }
        }
        //then verify the proofs
        for (int k = 0; k < numberOfProofs; k++) {
            verificationResult = verifyInteractiveZKP(originalProblem, helperProblems.get(k), challenges.get(k), proofs.get(k));
            if (!verificationResult) {
                if (log.isDebugEnabled()) {
                    log.debug("Error in verifying proof: " + k + " in the non-interactive proof.");
                }
                return false;
            }
        }
        return verificationResult;
    }

    @Override
    //todo: add meta data if there is any, to be included in the hash
    public boolean verifyNonInteractiveZKPWithSignature(PedersenCommitment originalProblem, List<PedersenCommitment> helperProblems,
                                                        byte[] message, List<BigInteger> challenges,
                                                        List<PedersenCommitmentProof> proofs) throws NoSuchAlgorithmException {
        boolean verificationResult = false;
        //first, verify the challenges
        List<BigInteger> derivedChallenges = this.createChallengeForNonInteractiveZKPWithSignature(originalProblem, helperProblems, message);
        for (int j = 0; j < numberOfProofs; j++) {
            BigInteger derivedChallenge = derivedChallenges.get(j);
            BigInteger originalChallenge = challenges.get(j);
            if (originalChallenge.compareTo(derivedChallenge) != 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Error in provided challenge: " + j + " for the non-interactive proof.");
                }
                return false;
            }
        }
        //then verify the proofs
        for (int k = 0; k < numberOfProofs; k++) {
            verificationResult = verifyInteractiveZKP(originalProblem, helperProblems.get(k), challenges.get(k), proofs.get(k));
            if (!verificationResult) {
                if (log.isDebugEnabled()) {
                    log.debug("Error in verifying proof: " + k + " in the non-interactive proof.");
                }
                return false;
            }
        }
        return verificationResult;
    }

    private byte[] obtainConcatenatedOriginalNHelperCommitments(PedersenCommitment originalCommitment, List<PedersenCommitment> helperCommitments) {

        //convert original problem and three helper problems into byte and concatenate them
        byte[] originalCommitmentBytes = originalCommitment.getCommitment().toByteArray();

        int concatenatedCommitmentsByteLength = 0;
        concatenatedCommitmentsByteLength += originalCommitmentBytes.length;

        List<byte[]> helperCommitmentsBytes = new ArrayList<>();
        for (PedersenCommitment helperCommitment : helperCommitments) {
            byte[] helperCommitmentBytes = helperCommitment.getCommitment().toByteArray();
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
        return concatenatedArray;
    }

    private List<BigInteger> obtainChallengesFromHash(byte[] hash) {
        //get the initial 160*3 bytes and create three challenges; such that each of them are in Zq.
        List<BigInteger> challenges = new ArrayList<>();
        int hashDestPos = 0;
        for (int i = 0; i < numberOfProofs; i++) {
            byte[] challengeBytes = new byte[lengthOfChallengeInBytes];
            System.arraycopy(hash, hashDestPos, challengeBytes, 0, lengthOfChallengeInBytes);
            //TODO:check if taking only the positive value, affects the collision-resistancy of hash output
            BigInteger challengeBigInt = new BigInteger(1, challengeBytes);
            challenges.add(challengeBigInt);
            hashDestPos += lengthOfChallengeInBytes;
        }
        return challenges;
    }
}