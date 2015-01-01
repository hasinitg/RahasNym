package org.rahasnym.api.idmapi;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.crypto.lib.zero.knowledge.proof.ZKPPedersenCommitment;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.communication.encdecoder.JSONPolicyDecoder;
import org.rahasnym.api.communication.policy.IDVPolicy;
import org.rahasnym.api.communication.policy.PolicyCombiner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 10:56 AM
 */

/**
 * This is the class that interfaces with the client application to perform pseudonymous identity management.
 */
public class ClientRequestHandler extends Thread {
    Socket clientSocket = null;

    public ClientRequestHandler(Socket socket) {
        super("ClientRequestHandler");
        this.clientSocket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            //read the policy from client
            String clientRequest = in.readLine();

            if (clientRequest != null) {

                IDMManager IDMManager = new IDMManager();
                String response = IDMManager.processClientMessage(clientRequest);
                //send response for IDTRequest from client.
                out.println(response);

                //wait for the client's response.
                String clientResponse = in.readLine();
                if (clientResponse != null) {

                    //process client response
                    String response2 = IDMManager.processClientMessage(clientResponse);

                    //if it is auth result, (in the case of non-interactive proof protocols), IDMM returns nothing.
                    if(response2 == null){
                        return;
                    }

                    //in the interactive protocol, send response (proof) to client:
                    out.println(response2);

                    //wait for auth result:
                    String ack = in.readLine();
                    if (ack != null) {
                        IDMManager.processClientMessage(ack);
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
        } catch (RahasNymException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            System.out.println("Error in parsing the JSON request.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
