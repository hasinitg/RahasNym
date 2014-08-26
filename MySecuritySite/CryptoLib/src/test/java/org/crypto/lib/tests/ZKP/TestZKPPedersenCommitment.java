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
            //client creates the real commitment (original problem) and a dummy commitment(helper problem)
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            PedersenPublicParams publicParams = pedersenCommitmentFactory.initialize();

            BigInteger value = CryptoUtil.getCommittableThruHash("hasi7786@gmail.com",
                    publicParams.getQ().bitLength() - 1);
            BigInteger secret = CryptoUtil.getCommittableThruPBKDF("321@$%^", publicParams.getQ().bitLength() - 1, 1000);
            PedersenCommitment originalCommitment = pedersenCommitmentFactory.createCommitment(value, secret);

            ZKPPedersenCommitment zkpPedersenClient = new ZKPPedersenCommitment(publicParams);
            PedersenCommitment helperCommitment = zkpPedersenClient.createHelperProblem(null);

            //two secrets are stored in the commitment object because it will be used when creating proof.
            originalCommitment.setX(value);
            System.out.println("X: " + value);
            originalCommitment.setR(secret);
            System.out.println("R: " + secret);
            System.out.println("C: " + originalCommitment.getCommitment());
            System.out.println();
            //verifier, creates a challenge on its side (assume public params are sent to him)
            ZKPPedersenCommitment zkpPedersenServer = new ZKPPedersenCommitment(publicParams);
            BigInteger challenge = zkpPedersenServer.createInteractiveChallenge();
            System.out.println("Challenge: " + challenge);

            //client creates the proof based on those two.
            PedersenCommitmentProof proof = zkpPedersenClient.createInteractiveProof(originalCommitment,
                    helperCommitment, challenge);

            //verifier verifies the proof
            boolean success = zkpPedersenServer.verifyInteractiveProof(originalCommitment, helperCommitment, challenge, proof);
            Assert.assertEquals(true, success);

        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Commitment creation failed.");
        } catch (CryptoAlgorithmException e) {
            Assert.fail("Commitment creation failed.");
        } catch (InvalidKeySpecException e) {
            Assert.fail("Error in creating commitable value ");
        }
    }
}
