package org.crypto.lib.tests.ZKP;

import junit.framework.Assert;
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
            String email = "hasi7786@gmail.com";
            String password = "321@$%^";

            //client initializes the pedersen commitment factory in order to create commitments.
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();

            //convert the above string values into committable values in the Zq domain.
            BigInteger value = CryptoUtil.getCommittableThruHash(email, publicParams.getQ().bitLength() - 1);
            byte[] salt = CryptoUtil.generateSalt(8);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);

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
            BigInteger secretD = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);
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
            String email = "hasi7786@gmail.com";
            String password = "321@$%^";

            /********************* Commitment creation phase ***************************/
            //client initializes the pedersen commitment factory in order to create commitments.
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = null;

            publicParams = pedersenCommitmentFactory.initialize();


            //convert the above string values into committable values in the Zq domain.
            BigInteger value = CryptoUtil.getCommittableThruHash(email, publicParams.getQ().bitLength() - 1);
            byte[] salt = CryptoUtil.generateSalt(8);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);

            //client creates the real commitment (original problem)
            PedersenCommitment originalCommitment = pedersenCommitmentFactory.createCommitment(value, secret);

            /********************** Proof creation phase **********************************/
            //first, create the helper problems.
            ZKPPedersenCommitment zkpPedersenClient = new ZKPPedersenCommitment(publicParams);
            List<PedersenCommitment> helperProblems = zkpPedersenClient.createHelperProblems(null);

            //since it is non-interactive proof, client creates the challenges by itself.
            List<BigInteger> challenges = zkpPedersenClient.createChallengeForNonInteractiveZKP(originalCommitment, helperProblems);

            /*create the proofs based on the above derived challenges. Usually during the proof creation, client has to re-derive
            the committable values from the original strings because the ones derived at commitment creation time
            are not stored anywhere due to security reasons.*/
            BigInteger valueD = CryptoUtil.getCommittableThruHash(email, publicParams.getQ().bitLength() - 1);
            BigInteger secretD = CryptoUtil.getCommittableThruPBKDF(password, salt, publicParams.getQ().bitLength() - 1, 1000);
            PedersenCommitment privateCommitment = new PedersenCommitment();
            privateCommitment.setX(valueD);
            privateCommitment.setR(secretD);

            List<PedersenCommitmentProof> proofs = zkpPedersenClient.createProofForNonInteractiveZKP(privateCommitment, helperProblems, challenges);

            /********************** Proof verification phase ******************************/
            //server verifies the proof.
            ZKPPedersenCommitment zkpServer = new ZKPPedersenCommitment(publicParams);
            boolean success = zkpServer.verifyNonInteractiveZKP(originalCommitment, helperProblems, challenges, proofs);
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

    }
}
