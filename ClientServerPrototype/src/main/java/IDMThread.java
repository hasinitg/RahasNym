import com.sun.deploy.util.BufferUtil;
import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/9/14
 * Time: 10:12 AM
 */
public class IDMThread extends Thread {
    Socket clientSocket = null;

    public IDMThread(Socket socket) {
        super("IDMThread");
        this.clientSocket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String fromClient = null;
            //while ((fromClient = in.readLine()) != null) {
            fromClient = in.readLine();
            System.out.println("IDM: heard from client: " + fromClient);
            //get the policy sent by client
            if (fromClient != null && fromClient.equals("policy")) {
                //TODO: do policy combination and find a matching IDT.
                // If there is no matching IDT, then obtain a token from IDP, prompt the user accordingly.
                //for the moment, create the IDT here.
                String email = "hasinitg@gmail.com";
                String password = "543&*^";
                byte[] salt = CryptoUtil.generateSalt(8);
                PedersenCommitmentFactory factory = new PedersenCommitmentFactory();
                PedersenPublicParams params = factory.initialize();
                String paramsString = params.getP().toString() + "," + params.getQ().toString() + "," + params.getG().toString() +
                        "," + params.getH().toString();
                BigInteger emailBIG = CryptoUtil.getCommittableThruHash(email, params.getQ().bitLength() - 1);
                BigInteger secretBIG = CryptoUtil.getCommittableThruPBKDF(password, salt, params.getQ().bitLength() - 1,
                        CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);
                PedersenCommitment commitment = factory.createCommitment(emailBIG, secretBIG);
                BigInteger commitmentBIG = commitment.getCommitment();
                String response = Constants.ZKP_I + "," + commitmentBIG + "," + paramsString;
                out.println(response);
                //wait for the challenge : should also obtain session id to identify which commitment
                //(should avoid memory leaks when storing session ids in-memory)
                fromClient = in.readLine();
                if (fromClient != null) {
                    System.out.println("IDM: challenge : " + fromClient);
                    //create the proof
                    ZKPPedersenCommitment zkpIDM = new ZKPPedersenCommitment(params);
                    PedersenCommitment helperCommitment = zkpIDM.createHelperProblem(null);
                    BigInteger challengeBIG = new BigInteger(fromClient, 10);
                    commitment.setX(emailBIG);
                    commitment.setR(secretBIG);
                    PedersenCommitmentProof proof = zkpIDM.createProofForInteractiveZKP(commitment, helperCommitment, challengeBIG);
                    boolean success = zkpIDM.verifyInteractiveZKP(commitment, helperCommitment, challengeBIG, proof);
                    if (success) {
                        System.out.println("Server : proof OK at server.");
                    }
                    //send to client:
                    out.println(proof.getU().toString() + "," + proof.getV().toString() + "," + helperCommitment.getCommitment().toString());
                    //wait for ack:
                    String ack = in.readLine();
                    if (ack != null && ack.equals("thanks.")) {
                        return;
                    } else {
                        //record that an error occurred and (in logs) and return.
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in obtaining input/output streams from client socket.");
            e.printStackTrace();
        } catch (CryptoAlgorithmException e) {
            System.out.println("Error in initializing pedersen factory.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in pederson commitment creation.");
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            System.out.println("Error in deriving secret to create the commitment.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
