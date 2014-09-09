package org.crypto.lib.perfTests;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/4/14
 * Time: 9:47 AM
 */
public class ZKP_NI_S {
    private static int DATA_SET_SIZE = 1000;
    private static int RECEIPT_SIZE = 1024 * 200; //10KB
    //initialize public params:
    private static PedersenPublicParams publicParams;
    private static PedersenCommitmentFactory pedersenCommitmentFactory;
    private static int bitLengthOfElementInZq;

    //output file:
    private static String RESULT_FILE_NAME = "./Results_ZKP_NI_S.txt";
    private static PrintStream printer;

    private static ZKPPedersenCommitment zkpClient;
    private static ZKPPedersenCommitment zkpServer;

    public static void main(String[] args) throws CryptoAlgorithmException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            //initialization of pedersen factory:
            pedersenCommitmentFactory = new PedersenCommitmentFactory();
            publicParams = pedersenCommitmentFactory.initialize();
            bitLengthOfElementInZq = publicParams.getQ().bitLength() - 1;

            //initialize zero-knowledge-proof
            zkpClient = new ZKPPedersenCommitment(publicParams);
            zkpServer = new ZKPPedersenCommitment(publicParams);

            //initialization of the results file:
            printer = new PrintStream(new FileOutputStream(new File(RESULT_FILE_NAME)));

            //create data set (secret value, password pairs)
            Map<String, byte[]> dataSet = createDataSet(DATA_SET_SIZE);
            //create messages
            List<byte[]> receipts = createReceipts(RECEIPT_SIZE);

            //measure time for deriving committable values and for creating the commitments
            List<PedersenCommitment> originalCommitments = measureTimeForCommitmentCreation(dataSet);

            //measure time for creating helper commitments (it will be usually 3*the time observed in ZKP_I case)
            List<PedersenCommitment> helperCommitments = measureTimeForHelperCommitmentCreation();

            //measure time for creating the challenges
            List<BigInteger> challenges = measureTimeForChallengeCreation(originalCommitments, helperCommitments, receipts);

            //measure time for creating the non-interactive proofs
            List<PedersenCommitmentProof> proofs = measureTimeForProofCreation(dataSet, helperCommitments, challenges);

            //measure time for verifying the non-interactive proofs
            measureTimeForProofVerification(originalCommitments, helperCommitments, receipts, challenges, proofs);
        } catch (FileNotFoundException e) {
            System.out.println("Error in writing to the file.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error in verifying the proofs.");
            e.printStackTrace();
        }
    }

    private static Map<String, byte[]> createDataSet(int sizeOfDataSet) {

        String credit_card_no = "6754-9786-1234-";
        String password = "321@$%^";

        LinkedHashMap<String, byte[]> values = new LinkedHashMap<>();
        for (int i = 0; i < sizeOfDataSet; i++) {
            String trailer = String.format("%04d", i);
            String currentCCN = credit_card_no + trailer;
            String currentPassword = password + trailer;
            byte[] salt = CryptoUtil.generateSalt(8);
            String userInputs = currentCCN + ":" + currentPassword;
            values.put(userInputs, salt);
        }
        return values;
    }

    private static List<byte[]> createReceipts(int receiptSize) {
        List<byte[]> receipts = new ArrayList<>();
        for (int u = 0; u < DATA_SET_SIZE; u++) {
            //generate random receipt bytes.
            byte[] randomReceiptBytes = new byte[(RECEIPT_SIZE)];
            new Random().nextBytes(randomReceiptBytes);
            receipts.add(randomReceiptBytes);
        }
        return receipts;
    }

    private static List<PedersenCommitment> measureTimeForCommitmentCreation(Map<String, byte[]> values)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

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
            BigInteger valueCommittable = CryptoUtil.getCommittableThruHash(value, bitLengthOfElementInZq);
            BigInteger secretCommittable = CryptoUtil.getCommittableThruPBKDF(
                    password, salt, bitLengthOfElementInZq, 1000);
            Long end = System.nanoTime();

            Long elapsedDerivationTime = end - start;
            elapsedDerivationTimes.add(elapsedDerivationTime);

            //now measure commitment creation time
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
        long averageDerivationTime = totalElapsedDerivationTime / DATA_SET_SIZE;
        //System.out.println("Average elapsed time for deriving committable values in nano seconds: " + averageDerivationTime);
        double avgDerTime = (double) averageDerivationTime / TestConstants.MILLIS_IN_NANO;
        String derivationTime = getTime() + ": Average elapsed time for deriving committable values in milli seconds: " + avgDerTime;
        System.out.println(derivationTime);
        printer.println(derivationTime);

        Long totalElapsedTime = 0L;
        for (Long elapsedCreationTime : elapsedCreationTimes) {
            totalElapsedTime += elapsedCreationTime;
        }
        long averageElapsedTime = totalElapsedTime / DATA_SET_SIZE;
        //System.out.println("Average elapsed time for computing commitment in nano seconds: " + averageElapsedTime);
        double avgElapsedTime = (double) averageElapsedTime / TestConstants.MILLIS_IN_NANO;
        String commitmentTime = getTime() + ": Average elapsed time for computing commitment in milli seconds: " + avgElapsedTime;
        System.out.println(commitmentTime);
        printer.println(commitmentTime);

        double totalAvgTime = avgDerTime + avgElapsedTime;
        String totalTime = getTime() + ": Total average time for creating the commitment in milli seconds: " + totalAvgTime;
        System.out.println(totalTime);
        printer.println(totalTime);

        return createdCommitments;
    }

    private static List<PedersenCommitment> measureTimeForHelperCommitmentCreation() {
        List<PedersenCommitment> helperCommitments = new ArrayList<>();
        List<Long> elapsedHelperCreationTime = new ArrayList<>();

        for (int j = 0; j < DATA_SET_SIZE; j++) {
            Long start = System.nanoTime();
            List<PedersenCommitment> randomCommitments = zkpClient.createHelperProblems(null);
            Long end = System.nanoTime();
            Long elapsedTime = end - start;
            elapsedHelperCreationTime.add(elapsedTime);
            //we know that three helper problems are created for NI_ZKP
            int size = randomCommitments.size();
            for (int k = 0; k < size; k++) {
                helperCommitments.add(randomCommitments.get(k));
            }
        }
        Long totalTime = 0L;
        for (Long elapsedTime : elapsedHelperCreationTime) {
            totalTime += elapsedTime;
        }
        Long averageTime = totalTime / DATA_SET_SIZE;
        double avgTime = (double) averageTime / TestConstants.MILLIS_IN_NANO;
        String timeString = getTime() + ": Average elapsed time for creating helper commitments in milli seconds: " + avgTime;
        System.out.println(timeString);
        printer.println(timeString);
        return helperCommitments;
    }

    private static List<BigInteger> measureTimeForChallengeCreation(List<PedersenCommitment> originalCommitments,
                                                                    List<PedersenCommitment> helperCommitments,
                                                                    List<byte[]> messages) throws NoSuchAlgorithmException {
        List<BigInteger> challenges = new ArrayList<>();
        List<Long> elapsedTimes = new ArrayList<>();
        for (int l = 0; l < DATA_SET_SIZE; l++) {
            PedersenCommitment originalCommitment = originalCommitments.get(l);
            byte[] currentMessage = messages.get(l);
            List<PedersenCommitment> randomCommitments = new ArrayList<>();
            for (int m = 0; m < CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP; m++) {
                int currentIndex = (l * 3) + m;
                randomCommitments.add(helperCommitments.get(currentIndex));
            }
            Long start = System.nanoTime();
            List<BigInteger> createdChallenges = zkpClient.createChallengeForNonInteractiveZKPWithSignature(
                    originalCommitment, randomCommitments, currentMessage);
            Long end = System.nanoTime();
            Long elapsed = end - start;
            elapsedTimes.add(elapsed);
            for (int n = 0; n < CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP; n++) {
                challenges.add(createdChallenges.get(n));
            }
        }
        reportTimes(elapsedTimes, "creating challenges");
        return challenges;
    }

    private static List<PedersenCommitmentProof> measureTimeForProofCreation(
            Map<String, byte[]> values, List<PedersenCommitment> helperCommitments, List<BigInteger> challenges)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        List<PedersenCommitmentProof> proofs = new ArrayList<>();
        List<Long> elapsedTimes = new ArrayList<>();
        ArrayList<Map.Entry<String, byte[]>> valuesList = new ArrayList<>(values.entrySet());
        for (int p = 0; p < DATA_SET_SIZE; p++) {
            List<PedersenCommitment> currentHelperCommitments = new ArrayList<>();
            List<BigInteger> currentChallenges = new ArrayList<>();
            for (int q = 0; q < CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP; q++) {
                currentHelperCommitments.add(helperCommitments.get((p * 3) + q));
                currentChallenges.add(challenges.get((p * 3) + q));
            }
            String currentValues = valuesList.get(p).getKey();
            String[] splittedVals = currentValues.split(":");
            String CCN = splittedVals[0];
            String password = splittedVals[1];
            byte[] currentSalt = valuesList.get(p).getValue();
            Long start = System.nanoTime();
            BigInteger valueCommittable = CryptoUtil.getCommittableThruHash(CCN, bitLengthOfElementInZq);
            BigInteger secretCommittable = CryptoUtil.getCommittableThruPBKDF(password, currentSalt, bitLengthOfElementInZq, 1000);
            PedersenCommitment originalCommitment = new PedersenCommitment();
            originalCommitment.setX(valueCommittable);
            originalCommitment.setR(secretCommittable);
            List<PedersenCommitmentProof> currentProofs = zkpClient.createProofForNonInteractiveZKP(
                    originalCommitment, currentHelperCommitments, currentChallenges);
            Long end = System.nanoTime();
            Long elapsed = end - start;
            elapsedTimes.add(elapsed);
            for (int r = 0; r < CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP; r++) {
                proofs.add(currentProofs.get(r));
            }
        }
        reportTimes(elapsedTimes, "creating proofs");
        return proofs;
    }

    private static void measureTimeForProofVerification(List<PedersenCommitment> originalCommitments,
                                                        List<PedersenCommitment> helperCommitments, List<byte[]> messages,
                                                        List<BigInteger> challenges,
                                                        List<PedersenCommitmentProof> proofs) throws Exception {
        List<Long> elapsedTimes = new ArrayList<>();
        for (int s = 0; s < DATA_SET_SIZE; s++) {
            PedersenCommitment originalCommitment = originalCommitments.get(s);
            byte[] message = messages.get(s);
            List<PedersenCommitment> currentHelperCommitments = new ArrayList<>();
            List<BigInteger> currentChallenges = new ArrayList<>();
            List<PedersenCommitmentProof> currentProofs = new ArrayList<>();
            for (int t = 0; t < CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP; t++) {
                currentHelperCommitments.add(helperCommitments.get((s * CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP) + t));
                currentChallenges.add(challenges.get((s * CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP) + t));
                currentProofs.add(proofs.get((s * CryptoLibConstants.NUMBER_OF_PROOFS_FOR_NI_ZKP) + t));
            }
            Long start = System.nanoTime();
            boolean success = zkpServer.verifyNonInteractiveZKPWithSignature(originalCommitment, currentHelperCommitments,
                    message, currentChallenges, currentProofs);
            Long end = System.nanoTime();
            Long elapsed = end - start;
            elapsedTimes.add(elapsed);
            if (!success) {
                System.out.println("Error in verifying the proofs.");
                throw new Exception("Error in verifying the proofs.");
            }
        }
        reportTimes(elapsedTimes, "verifying proofs");
    }

    private static void reportTimes(List<Long> elapsedTimes, String reportingString) {
        Long totalTime = 0L;
        for (Long elapsedTime : elapsedTimes) {
            totalTime += elapsedTime;
        }
        Long averageTime = totalTime / DATA_SET_SIZE;
        double avgTime = (double) averageTime / TestConstants.MILLIS_IN_NANO;
        String timeString = getTime() + ": Average elapsed time for " + reportingString + " in milli seconds: " + avgTime;
        System.out.println(timeString);
        printer.println(timeString);
    }

    private static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
