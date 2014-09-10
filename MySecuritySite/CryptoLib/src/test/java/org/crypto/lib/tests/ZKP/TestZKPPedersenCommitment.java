package org.crypto.lib.tests.ZKP;

import junit.framework.Assert;
import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.junit.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/25/14
 * Time: 10:22 AM
 */
public class TestZKPPedersenCommitment {
    /**
     * This test method simulates interactive zero-knowledge proof of knowledge protocol based on
     * pedersen commitment.
     */
    @Test
    public void testVerifyInteractiveProof() {
        try {
            /*Lets say we need to hide our email identity in pedersen commitment, of which we can prove the ownership
            later in Zero Knowledge.*/
            String email = "hasinitg@gmail.com";
            String password = "321@$%^";

            //client initializes the pedersen commitment factory in order to create commitments.
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();

            //convert the above string values into committable values in the Zq domain.
            BigInteger value = CryptoUtil.getCommittableThruHash(email, publicParams.getQ().bitLength() - 1);
            byte[] salt = CryptoUtil.generateSalt(CryptoLibConstants.DEFAULT_LENGTH_OF_SALT);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);

            //client creates the real commitment (original problem) and a dummy commitment(helper problem)
            PedersenCommitment originalCommitment = pedersenCommitmentFactory.createCommitment(value, secret);
            ZKPPedersenCommitment zkpPedersenClient = new ZKPPedersenCommitment(publicParams);
            PedersenCommitment helperCommitment = zkpPedersenClient.createHelperProblem(null);

            //verifier, creates a challenge on its side (assume public params are sent to him)
            ZKPPedersenCommitment zkpPedersenServer = new ZKPPedersenCommitment(publicParams);
            BigInteger challenge = zkpPedersenServer.createChallengeForInteractiveZKP();

            /*client creates the proof based on the challenge. Usually during the proof creation, client has to re-derive
            the committable values from the original strings because the ones derived at commitment creation time
            are not stored anywhere due to security reasons.*/
            BigInteger valueD = CryptoUtil.getCommittableThruHash(email, publicParams.getQ().bitLength() - 1);
            BigInteger secretD = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);
            PedersenCommitment dummyCommitment = new PedersenCommitment();
            dummyCommitment.setX(valueD);
            dummyCommitment.setR(secretD);
            PedersenCommitmentProof proof = zkpPedersenClient.createProofForInteractiveZKP(dummyCommitment,
                    helperCommitment, challenge);

            //verifier verifies the proof
            boolean success = zkpPedersenServer.verifyInteractiveZKP(originalCommitment, helperCommitment, challenge, proof);
            Assert.assertEquals(true, success);

        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Commitment creation failed.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Commitment creation failed.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Error in creating commitable value ");
        }
    }

    @Test
    public void testVerifyNonInteractiveZKP() {
        try {
            //while testing, print everything and see if the intermediate values make sense.
            //values to be committable:
            String bank_acc_no = "108-2-6745322185";
            String password = "321@$%^";

            /********************* Commitment creation phase ***************************/
            //client initializes the pedersen commitment factory in order to create commitments.
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();


            //convert the above string values into committable values in the Zq domain.
            BigInteger value = CryptoUtil.getCommittableThruHash(bank_acc_no, publicParams.getQ().bitLength() - 1);
            byte[] salt = CryptoUtil.generateSalt(8);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);

            //client creates the real commitment (original problem)
            PedersenCommitment originalCommitment = pedersenCommitmentFactory.createCommitment(value, secret);

            /********************** Proof creation phase **********************************/
            //first, create the helper commitments.
            ZKPPedersenCommitment zkpPedersenClient = new ZKPPedersenCommitment(publicParams);
            List<PedersenCommitment> helperCommitments = zkpPedersenClient.createHelperProblems(null);

            //since it is non-interactive proof, client creates the challenges by itself.
            List<BigInteger> challenges = zkpPedersenClient.createChallengeForNonInteractiveZKP(originalCommitment, helperCommitments);

            /*create the proofs based on the above derived challenges. Usually during the proof creation, client has to re-derive
            the committable values from the original strings because the ones derived at commitment creation time
            are not stored anywhere due to security reasons.*/
            BigInteger valueD = CryptoUtil.getCommittableThruHash(bank_acc_no, publicParams.getQ().bitLength() - 1);
            BigInteger secretD = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);
            PedersenCommitment privateCommitment = new PedersenCommitment();
            privateCommitment.setX(valueD);
            privateCommitment.setR(secretD);

            List<PedersenCommitmentProof> proofs = zkpPedersenClient.createProofForNonInteractiveZKP(privateCommitment,
                    helperCommitments, challenges);

            /********************** Proof verification phase ******************************/
            //server verifies the proof.
            ZKPPedersenCommitment zkpServer = new ZKPPedersenCommitment(publicParams);
            boolean success = zkpServer.verifyNonInteractiveZKP(originalCommitment, helperCommitments, challenges, proofs);
            Assert.assertEquals(true, success);

        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        }

    }

    @Test
    public void testVerifyNonInteractiveZKPWithSignature() {
        try {
            //while testing, print everything and see if the intermediate values make sense.
            //values to be committable:
            String credit_card_no = "6754-9786-1234-0987";
            String password = "321@$%^";
            //lets say average size of a e-receipt is 10KB
            byte[] randomReceiptBytes = new byte[(1024 * 10)];
            //generate random receipt bytes.
            new Random().nextBytes(randomReceiptBytes);

            /********************* Commitment creation phase ***************************/
            //client initializes the pedersen commitment factory in order to create commitments.
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();


            //convert the above string values into committable values in the Zq domain.
            BigInteger value = CryptoUtil.getCommittableThruHash(credit_card_no, publicParams.getQ().bitLength() - 1);
            byte[] salt = CryptoUtil.generateSalt(8);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);

            //client creates the commitment on credit_card_number
            PedersenCommitment originalCommitment = pedersenCommitmentFactory.createCommitment(value, secret);

            /********************** Proof creation phase **********************************/
            //first, create the helper commitments.
            ZKPPedersenCommitment zkpPedersenClient = new ZKPPedersenCommitment(publicParams);
            List<PedersenCommitment> helperCommitments = zkpPedersenClient.createHelperProblems(null);

            /*it is non-interactive-zero-knowledge with signature proof: client creates the challenges by itself,
            incorporating the message as well, in addition to the original commitment and the helper commitments.*/
            List<BigInteger> challenges = zkpPedersenClient.createChallengeForNonInteractiveZKPWithSignature(
                    originalCommitment, helperCommitments, randomReceiptBytes);

            /*create the proofs based on the above derived challenges. Usually during the proof creation, client has to re-derive
            the committable values from the original strings because the ones derived at commitment creation time
            are not stored anywhere due to security reasons.*/
            BigInteger valueD = CryptoUtil.getCommittableThruHash(credit_card_no, publicParams.getQ().bitLength() - 1);
            BigInteger secretD = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1,
                    CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);
            PedersenCommitment privateCommitment = new PedersenCommitment();
            privateCommitment.setX(valueD);
            privateCommitment.setR(secretD);

            //proof creation is same for both non-interactive-zk and non-interactive-zk-signature.
            List<PedersenCommitmentProof> proofs = zkpPedersenClient.createProofForNonInteractiveZKP(privateCommitment,
                    helperCommitments, challenges);

            /********************** Proof verification phase ******************************/
            //server verifies the proof.
            ZKPPedersenCommitment zkpServer = new ZKPPedersenCommitment(publicParams);
            //test with original message bytes.
            boolean success = zkpServer.verifyNonInteractiveZKPWithSignature(originalCommitment, helperCommitments,
                    randomReceiptBytes, challenges, proofs);
            Assert.assertEquals(true, success);
            //test with false message bytes.
            byte[] falseReceiptBytes = new byte[1024 * 10];
            new Random().nextBytes(falseReceiptBytes);
            boolean falseSuccess = zkpServer.verifyNonInteractiveZKPWithSignature(originalCommitment, helperCommitments,
                    falseReceiptBytes, challenges, proofs);
            Assert.assertEquals(false, falseSuccess);

        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Error in creating/verifying non interactive zero knowledge proof.");
        }
    }
}
