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
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This tests the performance of proof creation and proof verification wrt interactive zero-knowledge-proof.
 */
public class ZKP_I {
    private static int SIZE_OF_THE_DATASET = 1000;

    public static void main(String[] args) throws CryptoAlgorithmException, NoSuchAlgorithmException, InvalidKeySpecException {
        //initialization tasks:
        //create commitment factory
        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();
        //create ZKP objects for client and server
        ZKPPedersenCommitment zkpClient = new ZKPPedersenCommitment(publicParams);
        ZKPPedersenCommitment zkpServer = new ZKPPedersenCommitment(publicParams);

        /****************measure: commitment creation**********************/
        //create random identifiers and secrets
        Map<String, String> values = new HashMap<String, String>();
        for (int i = 0; i < SIZE_OF_THE_DATASET; i++) {
            String trailer = String.format("%04d", i);
            String email = "hasinitg" + trailer;
            //System.out.println(email);
            String password = "3214!@#" + trailer;
            values.put(email, password);
        }

        List<Long> elapsedCreationTimes = new ArrayList<Long>();
        List<PedersenCommitment> createdCommitments = new ArrayList<PedersenCommitment>();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value = entry.getKey();
            String password = entry.getValue();
            //measure time taken for create a commitment with each value and secret.
            Long startTime = System.nanoTime();
            BigInteger valueCommittable = CryptoUtil.getCommittableThruHash(value, publicParams.getQ().bitLength() - 1);
            BigInteger secretCommittable = CryptoUtil.getCommittableThruPBKDF(password, publicParams.getQ().bitLength() - 1, 1000);
            PedersenCommitment commitment = pedersenCommitmentFactory.createCommitment(valueCommittable, secretCommittable);
            Long endTime = System.nanoTime();
            Long elapsedTime = endTime - startTime;
            elapsedCreationTimes.add(elapsedTime);
            System.out.println("Elapsed Time: " + elapsedTime);

            //now add these created commitments to a list to be used in proof creation and proof verification
            commitment.setX(valueCommittable);
            commitment.setR(secretCommittable);
            createdCommitments.add(commitment);
        }
        Long totalElapsedTime = 0L;
        System.out.println("Initial total elapsed time: " + totalElapsedTime);
        for (Long elapsedCreationTime : elapsedCreationTimes) {
            totalElapsedTime += elapsedCreationTime;
        }
        long averageElapsedTime = totalElapsedTime / SIZE_OF_THE_DATASET;
        System.out.println("Average elapsed time in nano seconds: " + averageElapsedTime);
        //System.out.println("Average elapsed time in seconds: " + (double) TimeUnit.SECONDS.convert(averageElapsedTime, TimeUnit.NANOSECONDS));
        System.out.println("Average elapsed time in seconds: " + (double) averageElapsedTime / 1000000000.0);

        /************measure: proof creation************/

        //first, measure time for creating helper commitments from random x, r values.
        List<PedersenCommitment> helperCommitments = new ArrayList<PedersenCommitment>();
        List<Long> elapsedHelperCreationTime = new ArrayList<Long>();
        for (int k = 0; k < SIZE_OF_THE_DATASET; k++) {
            Long start = System.nanoTime();
            PedersenCommitment helperProblem = zkpClient.createHelperProblem(null);
            Long end = System.nanoTime();
            Long gap = end - start;
            System.out.println("Elapsed time for helper creation: " + gap);

            //put the created helpers also in a list, to be used in proof creation and verification
            helperCommitments.add(helperProblem);
        }
        //calculate the average time:


        /*********************************measure: proof verification***********************/


    }
}
