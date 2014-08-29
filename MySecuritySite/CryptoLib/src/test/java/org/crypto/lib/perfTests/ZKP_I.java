package org.crypto.lib.perfTests;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/26/14
 * Time: 12:39 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * This tests the performance of proof creation and proof verification wrt interactive zero-knowledge-proof.
 */
public class ZKP_I {
    private static int SIZE_OF_THE_DATASET = 1000;
    private static double MILLIS_IN_NANO = 1000000.0;

    public static void main(String[] args) throws CryptoAlgorithmException, NoSuchAlgorithmException, InvalidKeySpecException {
        //initialization tasks:
        //create commitment factory
        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();
        //create ZKP objects for client and server
        ZKPPedersenCommitment zkpClient = new ZKPPedersenCommitment(publicParams);
        ZKPPedersenCommitment zkpServer = new ZKPPedersenCommitment(publicParams);

        /****************measure: commitment creation**********************/

        //create random identifiers and passwords and salts to generate secrets.
        Map<String, byte[]> values = new LinkedHashMap<>();
        for (int i = 0; i < SIZE_OF_THE_DATASET; i++) {
            String trailer = String.format("%04d", i);
            String email = "hasinitg" + trailer;
            String password = "3214!@#" + trailer;
            byte[] salt = CryptoUtil.generateSalt(8);
            String combinedValues = email + ":" + password;
            values.put(combinedValues, salt);
        }

        List<Long> elapsedDerivationTimes = new ArrayList<>();
        List<Long> elapsedCreationTimes = new ArrayList<>();
        List<PedersenCommitment> createdCommitments = new ArrayList<>();

        for (Map.Entry<String, byte[]> entry : values.entrySet()) {
            String combinedValue = entry.getKey();
            String[] valArray = combinedValue.split(":");
            String value = valArray[0];
            String password = valArray[1];
            byte[] salt = entry.getValue();

            //measure time taken for deriving committable values and computing a commitment with each value and secret.
            Long start = System.nanoTime();
            BigInteger valueCommittable = CryptoUtil.getCommittableThruHash(value, publicParams.getQ().bitLength() - 1);
            BigInteger secretCommittable = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);
            Long end = System.nanoTime();

            Long elapsedDerivationTime = end - start;
            elapsedDerivationTimes.add(elapsedDerivationTime);

            Long startTime = System.nanoTime();
            PedersenCommitment commitment = pedersenCommitmentFactory.createCommitment(valueCommittable, secretCommittable);
            Long endTime = System.nanoTime();

            Long elapsedTime = endTime - startTime;
            elapsedCreationTimes.add(elapsedTime);

            //now add these created commitments to a list to be used in proof creation and proof verification
            createdCommitments.add(commitment);
        }
        Long totalElapsedDerivationTime = 0L;
        for (Long derivationTime : elapsedDerivationTimes) {
            totalElapsedDerivationTime += derivationTime;
        }
        long averageDerivationTime = totalElapsedDerivationTime / SIZE_OF_THE_DATASET;
        //System.out.println("Average elapsed time for deriving committable values in nano seconds: " + averageDerivationTime);
        double avgDerTime = (double) averageDerivationTime / MILLIS_IN_NANO;
        System.out.println("Average elapsed time for deriving committable values in milli seconds: " + avgDerTime);

        Long totalElapsedTime = 0L;
        for (Long elapsedCreationTime : elapsedCreationTimes) {
            totalElapsedTime += elapsedCreationTime;
        }
        long averageElapsedTime = totalElapsedTime / SIZE_OF_THE_DATASET;
        //System.out.println("Average elapsed time for computing commitment in nano seconds: " + averageElapsedTime);
        double avgElapsedTime = (double) averageElapsedTime / MILLIS_IN_NANO;
        System.out.println("Average elapsed time for computing commitment in milli seconds: " + avgElapsedTime);
        double totalAvgTime = avgDerTime + avgElapsedTime;
        System.out.println("Total average time for creating the commitment in milli seconds: " + totalAvgTime);


        /************measure: proof creation************/

        //first, measure time for creating helper commitments from random x, r values.
        List<PedersenCommitment> helperCommitments = new ArrayList<PedersenCommitment>();
        List<Long> elapsedHelperCreationTime = new ArrayList<Long>();
        for (int k = 0; k < SIZE_OF_THE_DATASET; k++) {
            Long start = System.nanoTime();
            PedersenCommitment helperProblem = zkpClient.createHelperProblem(null);
            Long end = System.nanoTime();

            Long gap = end - start;
            elapsedHelperCreationTime.add(gap);
            //put the created helpers also in a list, to be used in proof creation and verification
            helperCommitments.add(helperProblem);
        }
        //calculate the average time:
        Long totalHelperCreationTime = 0L;
        for (Long elapsedTime : elapsedHelperCreationTime) {
            totalHelperCreationTime += elapsedTime;
        }
        long avgHelperCreationTime = totalHelperCreationTime / SIZE_OF_THE_DATASET;
        //System.out.println("Average elapsed time for creating helper commitment in nano seconds: " + avgHelperCreationTime);
        System.out.println("Average elapsed time for creating helper commitment in milli seconds: " +
                (double) avgHelperCreationTime / MILLIS_IN_NANO);

        //create challenges to create proofs
        List<BigInteger> challenges = new ArrayList<BigInteger>();
        for (int l = 0; l < SIZE_OF_THE_DATASET; l++) {
            BigInteger challenge = zkpServer.createInteractiveChallenge();
            challenges.add(challenge);
        }

        //now create the proofs, measuring the times taken
        List<PedersenCommitmentProof> proofs = new ArrayList<PedersenCommitmentProof>();
        List<Long> proofCreationTimes = new ArrayList<Long>();
        ArrayList<Map.Entry<String, byte[]>> valuesList = new ArrayList<>(values.entrySet());
        for (int m = 0; m < SIZE_OF_THE_DATASET; m++) {
            PedersenCommitment helperCommitment = helperCommitments.get(m);
            BigInteger challenge = challenges.get(m);
            String combinedValue = valuesList.get(m).getKey();
            String[] valArray = combinedValue.split(":");
            String value = valArray[0];
            String password = valArray[1];
            byte[] salt = valuesList.get(m).getValue();

            Long start = System.nanoTime();
            /*here, we need to count the time taken for deriving the committable values from
              original identifier value and secret because they are not stored anywhere.
             */
            BigInteger valueCommittable = CryptoUtil.getCommittableThruHash(value, publicParams.getQ().bitLength() - 1);
            BigInteger secretCommittable = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);
            PedersenCommitment originalCommitment = new PedersenCommitment();
            originalCommitment.setX(valueCommittable);
            originalCommitment.setR(secretCommittable);
            PedersenCommitmentProof proof = zkpClient.createInteractiveProof(originalCommitment, helperCommitment, challenge);
            Long end = System.nanoTime();

            Long elapsedTime = end - start;
            proofCreationTimes.add(elapsedTime);
            proofs.add(proof);
        }
        //calculate the average proof creation time.
        Long totalProofCreationTime = 0L;
        for (Long proofCreationTime : proofCreationTimes) {
            totalProofCreationTime += proofCreationTime;
        }
        long averageProofCreationTime = totalProofCreationTime / SIZE_OF_THE_DATASET;
        //System.out.println("Average elapsed time for creating proofs in nano seconds: " + averageProofCreationTime);
        System.out.println("Average elapsed time for creating proofs in milli seconds: " +
                (double) averageProofCreationTime / MILLIS_IN_NANO);

        /*********************************measure: proof verification***********************/
        List<Long> proofVerificationTimes = new ArrayList<Long>();
        for (int n = 0; n < SIZE_OF_THE_DATASET; n++) {
            //obtain the necessary elements from each of the previously created lists
            PedersenCommitment originalCommitment = createdCommitments.get(n);
            PedersenCommitment helperCommitment = helperCommitments.get(n);
            BigInteger challenge = challenges.get(n);
            PedersenCommitmentProof proof = proofs.get(n);

            //start
            Long start = System.nanoTime();
            boolean success = zkpServer.verifyInteractiveProof(originalCommitment, helperCommitment, challenge, proof);
            //end
            Long end = System.nanoTime();

            Long elapsedTime = end - start;
            proofVerificationTimes.add(elapsedTime);
            if (!success) {
                System.out.println("Failure.");
            }
        }
        //calculate average:
        Long totalProofVerificationTime = 0L;
        for (Long proofVerificationTime : proofVerificationTimes) {
            totalProofVerificationTime += proofVerificationTime;
        }
        long averageProofVerificationTime = totalProofVerificationTime / SIZE_OF_THE_DATASET;
        //System.out.println("Average elapsed time for verifying proofs in nano seconds: " + averageProofVerificationTime);
        System.out.println("Average elapsed time for verifying proofs in milli seconds: " + (double) averageProofVerificationTime / MILLIS_IN_NANO);
    }
}
