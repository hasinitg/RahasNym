package org.rahasnym.spclient;

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.rahasnym.api.clientapi.ClientAPI;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/15/14
 * Time: 10:47 AM
 */
public class SPClientIDMProtocol {

    public String obtainIdentityToken(){
        //gets the policy from SP
        try (   //connects to IDMModule
                Socket clientSocket = new Socket(SPClientConstants.LOCAL_HOST, SPClientConstants.IDM_MODULE_PORT);
                //obtain output stream
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                //obtain input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            out.println("policy");
            String response = in.readLine();
            if (response != null) {
                System.out.println("Client: heard from IDM: " + response);
                //decode the response
                //decodes the first word from the array of strings separated by comma which should indicate which protocol to use.
                String[] responseArray = response.split(",");
                if (responseArray[0].equals(SPClientConstants.ZKP_I)) {
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
                        if(success){
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
            return null;
        } catch (IOException e) {
            //TODO: handle the exception properly
            e.printStackTrace();
        } catch (CryptoAlgorithmException e) {
            System.out.println("Error in creating pedersen commitment.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void connectToRemoteHost(){
        //try{
            ClientAPI clientAPI = new ClientAPI();
            //clientAPI.requestPolicy("http://localhost:8080/amazingshop/service/shop");

            /*HTTPClientRequest getR = new HTTPClientRequest();
            getR.setRequestType(SPClientConstants.RequestType.GET);
            getR.setRequestURI("http://localhost:8080/amazingshop/service/shop");
            getR.execute();*/

        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    public void openPolicyFile(){
        try {
            String path = "/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources";
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String fileContent = reader.readLine();
            System.out.println(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
