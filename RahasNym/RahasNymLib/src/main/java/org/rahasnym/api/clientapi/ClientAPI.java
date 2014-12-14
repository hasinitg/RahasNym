package org.rahasnym.api.clientapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/6/14
 * Time: 6:18 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.json.JSONObject;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.HTTPClientRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 * This is the API provided for the developers of verifier client - i.e: SPClient.
 */
public class ClientAPI {

    /*Request the identity verification policy from the SP*/
    public String requestPolicy(String url) {
        try {
            HTTPClientRequest getR = new HTTPClientRequest();
            getR.setRequestType(Constants.RequestType.GET);
            getR.setRequestURI(url);
            int status = getR.execute();
            //todo: check if it is a success status, if so, obtain the policy from the string. otherwise, throw an exception
            String responseString = getR.getResponseString();
            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Client application calls this to perform identity verification with SP according to the agreed policy
     * by both parties, in order to perform the intended operation.
     * This function returns to the client application, if there is an authenticated session given by SP upon successful
     * verification of the identity.
     * @param spPolicy
     * @param operation
     * @param spURL
     * @param receipt
     * @return
     */
    public String authenticate(String spPolicy, String operation, String spURL, String receipt) throws RahasNymException {
        //gets the policy from SP
        //TODO: should initialize the port from the configuration of the client device
        int IDMMPort =  Constants.IDM_MODULE_PORT;
        try (
                //todo: read this from configuration
                //connects to IDMModule
                Socket clientSocket = new Socket(Constants.LOCAL_HOST, IDMMPort);
                //obtain output stream
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                //obtain input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            //create the request:
            JSONObject request = new JSONObject();
            request.append(Constants.REQUEST_TYPE, Constants.IDT_REQUEST);
            request.append(Constants.OPERATION, operation);
            request.append(Constants.VERIFIER_POLICY, spPolicy);
            request.append(Constants.TRANSACTION_RECEIPT, receipt);
            //send the policy
            out.println(request.toString());
            //read the token
            String response = in.readLine();
            if (response != null) {
                System.out.println("Client: heard from IDM: " + response);
                //decode the response
                //decodes the first word from the array of strings separated by comma which should indicate which protocol to use.
                String[] responseArray = response.split(",");
                if (responseArray[0].equals(Constants.ZKP_I)) {
                    //obtain the commitment & public params from the response, send it to the server and obtain the challenge
                    String commitmentString = responseArray[1];
                    //*****TODO: replace challenge creation with obtaining challenge from SP
                    BigInteger commitment = new BigInteger(commitmentString);
                    PedersenCommitment originalCommitment = new PedersenCommitment();
                    originalCommitment.setCommitment(commitment);
                    PedersenPublicParams params = new PedersenPublicParams();
                    params.setP(new BigInteger(responseArray[2]));
                    params.setQ(new BigInteger(responseArray[3]));
                    params.setG(new BigInteger(responseArray[4]));
                    params.setH(new BigInteger(responseArray[5]));
                    PedersenCommitmentFactory factory = new PedersenCommitmentFactory();
                    factory.initialize(params);
                    ZKPPedersenCommitment clientZKP = new ZKPPedersenCommitment(params);
                    BigInteger challenge = clientZKP.createChallengeForInteractiveZKP();
                    //*****TODO:close
                    //send the challenge (should send something indicating the session as well.)
                    out.println(challenge.toString());
                    //listen for the proofs:
                    String proofString = in.readLine();
                    if (proofString != null) {
                        System.out.println("Client: proof string: " + proofString);
                        //****TODO: send proof to the SP.
                        String[] proofs = proofString.split(",");
                        //decode everything
                        String u = proofs[0];
                        String v = proofs[1];
                        BigInteger uBIG = new BigInteger(u);
                        BigInteger vBIG = new BigInteger(v);
                        BigInteger helper = new BigInteger(proofs[2]);
                        PedersenCommitment helperCommitment = new PedersenCommitment();
                        helperCommitment.setCommitment(helper);
                        PedersenCommitmentProof proof = new PedersenCommitmentProof();
                        proof.setU(uBIG);
                        proof.setV(vBIG);
                        boolean success = clientZKP.verifyInteractiveZKP(originalCommitment, helperCommitment, challenge, proof);
                        //****TODO: close
                        //send thanks to server
                        if (success) {
                            System.out.println("Protocol success.");
                            out.println("Thanks.");
                        } else {
                            System.out.println("Error.");
                            out.println("Error.");
                        }
                    } else {
                        out.println("Error.");
                    }
                }
            }
            throw new RahasNymException("Response from IDMM is null.");
        } catch (IOException e) {
            //TODO: handle the exception properly
            e.printStackTrace();
        } catch (CryptoAlgorithmException e) {
            System.out.println("Error in creating pedersen commitment.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            System.out.println("Error in creating the IDT request.");
            e.printStackTrace();
        }
        return null;
    }

    public String submitToken() {
        return null;
    }

}
