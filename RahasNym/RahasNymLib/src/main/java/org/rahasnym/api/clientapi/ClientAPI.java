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
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.Util;
import org.rahasnym.api.communication.HTTPClientRequest;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;
import org.rahasnym.api.verifierapi.VerifierAPI;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

/**
 * This is the API provided for the developers of verifier client - i.e: SPClient.
 */
public class ClientAPI {

    /*Request the identity verification policy from the SP*/
    public String requestPolicy(String url) throws RahasNymException {
        try {
            HTTPClientRequest getR = new HTTPClientRequest();
            getR.setRequestType(Constants.RequestType.GET);
            getR.setRequestURI(url);
            int status = getR.execute();
            //todo: check if it is a success status, if so, obtain the policy from the string. otherwise, throw an exception
            String responseString = getR.getResponseString();
            return responseString;
        } catch (IOException e) {
            throw new RahasNymException(e.getMessage());
        }
    }

    /*This is to be used in test cases run during build time.*/
    /*public String requestPolicyInVM(String policyPath) throws IOException {
        VerifierAPI verifier = new VerifierAPI();
        String policy = verifier.getIDVPolicy(policyPath);
        return policy;
    }*/

    public String requestPolicyWithReceipt(String url) throws RahasNymException {
        try {
            HTTPClientRequest getR = new HTTPClientRequest();
            getR.setRequestType(Constants.RequestType.GET);
            getR.setRequestURI(url);
            getR.setRequestHeader(Constants.RECEIPT_REQUIRED, Constants.TRUE);
            int status = getR.execute();
            //todo: check if it is a success status, if so, obtain the policy from the string. otherwise, throw an exception
            InputStream responseString = getR.getResponseBodyAsStream();
            return Util.convertInputStreamToString(responseString);
        } catch (IOException e) {
            throw new RahasNymException(e.getMessage());
        }

    }

    /*This is only for the test cases run during build time.*/
    /*public String requestPolicyWithReceiptInVM(String policyPath) throws IOException {
        VerifierAPI verifier = new VerifierAPI();
        String policy = verifier.getIDVPolicy(policyPath);
        return policy;
    }*/

    /**
     * Client application calls this to perform identity verification with SP according to the agreed policy
     * by both parties, in order to perform the intended operation.
     * This function returns to the client application, if there is an authenticated session given by SP upon successful
     * verification of the identity.
     *
     * @param authInfo@return
     */
    public String authenticate(AuthInfo authInfo) throws RahasNymException {
        //TODO: should initialize the port from the configuration of the client device
        int IDMMPort = Constants.IDM_MODULE_PORT;
        try (
                //todo: read this from configuration
                //connects to IDMModule
                Socket clientSocket = new Socket(Constants.LOCAL_HOST, IDMMPort);
                //obtain output stream
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                //obtain input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String spURL = authInfo.getSpURL();
            //create the request:
            JSONObject request = new JSONObject();
            String sessionID;
            if (authInfo.getSessionID() != null) {
                sessionID = authInfo.getSessionID();
                request.put(Constants.SESSION_ID, sessionID);
            }

            request.put(Constants.REQUEST_TYPE, Constants.IDT_REQUEST);
            request.put(Constants.OPERATION, authInfo.getOperation());
            request.put(Constants.VERIFIER_POLICY, authInfo.getPolicy());
            request.put(Constants.PSEUDONYM_WITH_SP, authInfo.getPseudonym());
            if (authInfo.getReceipt() != null) {
                request.put(Constants.TRANSACTION_RECEIPT, authInfo.getReceipt());
            }
            //send the policy
            out.println(request.toString());
            //read the response
            String response = in.readLine();
            if (response != null) {
                //TODO: add these sysouts to debug logs.
                System.out.println("Client: heard from IDM: " + response);
                //forward the response to Verifier
                VerifierAPI verifierAPI = new VerifierAPI();

                String verifierResponse1 = sendIDVRequestToVerifier(response, spURL);
                System.out.println("Client: heard from Verifier: " + verifierResponse1);
                //identify the response type:
                JSONObject verifierResponse1JSON = new JSONObject(new JSONTokener(verifierResponse1));
                boolean respSentToIDMM = false;
                if (Constants.AUTH_CHALLENGE.equals(verifierResponse1JSON.optString(Constants.REQUEST_TYPE))) {
                    //hand over to IDMM and expect response
                    out.println(verifierResponse1);
                    String challengeResponse = in.readLine();

                    String verifierResponse2 = sendIDVRequestToVerifier(challengeResponse, spURL);

                    out.println(verifierResponse2);
                    respSentToIDMM = true;
                    verifierResponse1 = verifierResponse2;
                }
                //decode auth result
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                String result = encoderDecoder.decodeAuthResult(verifierResponse1);
                if (!respSentToIDMM) {
                    out.println(verifierResponse1);
                }
                return result;

            }
            throw new RahasNymException("Response from IDMM is null.");
        } catch (IOException e) {
            //TODO: handle the exception properly
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("Error in creating the IDT request.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This is to be used only in the test cases run during the execution time.
     * @param authInfo
     * @return
     * @throws RahasNymException
     */
    public String authenticateINVM(AuthInfo authInfo) throws RahasNymException {
        //TODO: should initialize the port from the configuration of the client device
        int IDMMPort = Constants.IDM_MODULE_PORT;
        try (
                //todo: read this from configuration
                //connects to IDMModule
                Socket clientSocket = new Socket(Constants.LOCAL_HOST, IDMMPort);
                //obtain output stream
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                //obtain input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String spURL = authInfo.getSpURL();
            //create the request:
            JSONObject request = new JSONObject();
            String sessionID;
            if (authInfo.getSessionID() != null) {
                sessionID = authInfo.getSessionID();
                request.put(Constants.SESSION_ID, sessionID);
            }

            request.put(Constants.REQUEST_TYPE, Constants.IDT_REQUEST);
            request.put(Constants.OPERATION, authInfo.getOperation());
            request.put(Constants.VERIFIER_POLICY, authInfo.getPolicy());
            request.put(Constants.PSEUDONYM_WITH_SP, authInfo.getPseudonym());
            request.put(Constants.IS_IN_VM, Constants.TRUE);
            if (authInfo.getReceipt() != null) {
                request.put(Constants.TRANSACTION_RECEIPT, authInfo.getReceipt());
            }
            //send the policy
            out.println(request.toString());
            //read the response
            String response = in.readLine();
            if (response != null) {
                //TODO: add these sysouts to debug logs.
                //forward the response to Verifier
                VerifierAPI verifierAPI = new VerifierAPI();
                String verifierResponse1 = verifierAPI.handleIDVReqMessage(response, null);

                //identify the response type:
                JSONObject verifierResponse1JSON = new JSONObject(new JSONTokener(verifierResponse1));
                boolean respSentToIDMM = false;
                if (Constants.AUTH_CHALLENGE.equals(verifierResponse1JSON.optString(Constants.REQUEST_TYPE))) {
                    //hand over to IDMM and expect response
                    out.println(verifierResponse1);
                    String challengeResponse = in.readLine();

                    String verifierResponse2 = verifierAPI.handleIDVReqMessage(challengeResponse, authInfo.getReceipt());

                    out.println(verifierResponse2);
                    respSentToIDMM = true;
                    verifierResponse1 = verifierResponse2;
                }
                //decode auth result
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                //String result = encoderDecoder.decodeAuthResult(verifierResponse1);
                if (!respSentToIDMM) {
                    out.println(verifierResponse1);
                }
                //returns the response sent by the verifier so that the actual client application can decode as required.
                return verifierResponse1;
            }
            throw new RahasNymException("Response from IDMM is null.");
        } catch (IOException e) {
            //TODO: handle the exception properly
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("Error in creating the IDT request.");
            e.printStackTrace();
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private String sendIDVRequestToVerifier(String IDVRequest, String verifierURL) throws IOException {
        HTTPClientRequest postR = new HTTPClientRequest();
        postR.setRequestType(Constants.RequestType.CREATE);
        postR.setRequestURI(verifierURL);
        postR.setPayLoad(IDVRequest);
        int status = postR.execute();
        String response = postR.getResponseString();
        return response;
    }

}
