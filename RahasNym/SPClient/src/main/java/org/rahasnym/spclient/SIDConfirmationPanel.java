package org.rahasnym.spclient;

import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.clientapi.ClientAPI;
import org.rahasnym.api.idenity.IdentityMessagesEncoderDecoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 2/10/15
 * Time: 12:17 AM
 */
public class SIDConfirmationPanel extends JPanel {
    private String GET_IDT_COMMAND = "Get IDT";
    private String SUBMIT_IDT_COMMAND = "Submit IDT";
    private String GET_PROOF_COMMAND = "Get Proof";
    private String SUBMIT_POOF_COMMAND = "Submit Proof";

    private JApplet parentApplet = null;

    private JLabel studentIDLabel;
    private JTextField studentIDTxtField;

    private JButton getIDTBttn;
    private JButton submitIDTBttn;

    private JLabel challengeLabel;
    private JTextField challengeTxtField;

    private JButton getProofButton;
    private JButton submitProofButton;

    private JLabel proofLabel;
    private JTextField proofTxtField;

    /**
     * since the SP application knows that Student ID is verified with ZKP_I, the UI is set accordingly, to support the
     * order of interactions in ZKP_I
     */
    private String sessionIDAtRahasNym;
    private String sessionIDAtSP;

    String pseudonym;

    public SIDConfirmationPanel(JApplet applet) {
        parentApplet = applet;
        studentIDLabel = new JLabel("Student ID:");
        studentIDTxtField = new JTextField("");
        getIDTBttn = new JButton(GET_IDT_COMMAND);
        getIDTBttn.addActionListener(new GetIDTActionListener());
        submitIDTBttn = new JButton(SUBMIT_IDT_COMMAND);
        submitIDTBttn.addActionListener(new SubmitIDTActionListener());

        challengeLabel = new JLabel("Challenge:");
        challengeTxtField = new JTextField("");

        getProofButton = new JButton(GET_PROOF_COMMAND);
        getProofButton.addActionListener(new GetProofActionListener());

        proofLabel = new JLabel("Identity Proof:");
        proofTxtField = new JTextField("");

        submitProofButton = new JButton(SUBMIT_POOF_COMMAND);
        submitProofButton.addActionListener(new SubmitProofActionListener());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(getIDTBttn);
        buttonPanel.add(submitIDTBttn);

        JPanel jPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        //1st row
        jPanel.add(studentIDLabel);
        jPanel.add(studentIDTxtField);
        //2nd row
        jPanel.add(new JLabel(""));
        jPanel.add(buttonPanel);
        //3rd row
        jPanel.add(challengeLabel);
        jPanel.add(challengeTxtField);
        //4th row
        jPanel.add(new JLabel(""));
        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel2.add(getProofButton);
        buttonPanel2.add(new JLabel(""));
        jPanel.add(buttonPanel2);
        //5th row
        jPanel.add(proofLabel);
        jPanel.add(proofTxtField);
        //6th row
        jPanel.add(new JLabel(""));
        JPanel buttonPanel3 = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel3.add(submitProofButton);
        buttonPanel3.add(new JLabel(""));
        jPanel.add(buttonPanel3);

        add(jPanel, BorderLayout.CENTER);
    }

    private class GetIDTActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            pseudonym = parentApplet.getParameter(Constants.USER_NAME);
            try {
                //Get the policy
                String policy = PolicyHolder.getInstance().getPolicy();
                ClientAPI clientAPI = new ClientAPI();
                if (policy == null) {
                    policy = clientAPI.requestPolicy(SPClientConstants.SP_URL);
                    JSONObject spResp = new JSONObject(new JSONTokener(policy));
                    policy = spResp.optString(org.rahasnym.api.Constants.POLICY);
                    PolicyHolder.getInstance().setPolicy(policy);
                }
            } catch (RahasNymException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            //we know that email proof will always be non-interactive. Hence the applet implements the client API tasks.
            try (
                    //connects to IDMModule
                    Socket clientSocket = new Socket(Constants.LOCAL_HOST, Constants.IDM_MODULE_PORT);
                    //obtain output stream
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    //obtain input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                //create the request:
                JSONObject request = new JSONObject();
                request.put(Constants.REQUEST_TYPE, Constants.IDT_REQUEST);
                request.put(Constants.OPERATION, SPClientConstants.FREE_SHIPPING_MEMBERSHIP);
                request.put(Constants.VERIFIER_POLICY, PolicyHolder.getInstance().getPolicy());
                request.put(Constants.PSEUDONYM_WITH_SP, pseudonym);
                //send the IDT request
                out.println(request.toString());
                //read the response
                String response = in.readLine();
                if (response != null) {
                    //TODO: add these sysouts to debug logs.
                    System.out.println("Client: heard from IDM: " + response);
                    //read and remove session ID that has been set by RahasNym
                    JSONObject respObj = new JSONObject(new JSONTokener(response));
                    sessionIDAtRahasNym = respObj.optString(Constants.SESSION_ID);
                    respObj.remove(Constants.SESSION_ID);
                    studentIDTxtField.setText(respObj.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "Identity Token is null.", "IDT is null", JOptionPane.ERROR_MESSAGE);
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Could not connect to IDMM.", "IDMM connection failed.", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error obtaining IDT. Please check if IDMM is running.", "Error obtaining IDT.", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in creating the IDT request.", "Error obtaining IDT.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SubmitIDTActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String loggedInSessionID = parentApplet.getParameter(Constants.SESSION_ID);
                HTTPClientRequest freeshipReq = new HTTPClientRequest();
                freeshipReq.setRequestType(SPClientConstants.RequestType.CREATE);
                freeshipReq.setRequestURI("http://localhost:8080/amazingshop/service/freeshipping");
                freeshipReq.setRequestHeader(SPClientConstants.LOGGED_IN_SESSION_ID, loggedInSessionID);
                JSONObject jsonReq = new JSONObject(studentIDTxtField.getText());
                jsonReq.put(Constants.USER_NAME, pseudonym);

                freeshipReq.setPayLoad(jsonReq.toString());
                int status = freeshipReq.execute();

                if (status == Constants.HTTP_ERROR_CODE) {
                    JOptionPane.showMessageDialog(null, "Error in signing up. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    studentIDTxtField.setText("");
                    return;
                }
                //we expect auth challenge here.
                String freeshipResponse = freeshipReq.getResponseString();
                //set in challenge text field
                challengeTxtField.setText(freeshipResponse);

            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in sending the sign up request.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in creating the sign up request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private class GetProofActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            //send to IDMM to get the auth challenge response
            try (
                    //connects to IDMModule
                    Socket clientSocket = new Socket(Constants.LOCAL_HOST, Constants.IDM_MODULE_PORT);
                    //obtain output stream
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    //obtain input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                //extract and remove the session ID set by SP and replace it with the session ID set by IDMM
                JSONObject respObject = new JSONObject(new JSONTokener(challengeTxtField.getText()));
                sessionIDAtSP = respObject.optString(Constants.SESSION_ID);
                respObject.remove(Constants.SESSION_ID);
                respObject.put(Constants.SESSION_ID, sessionIDAtRahasNym);
                respObject.remove(Constants.REQUEST_TYPE);
                respObject.put(Constants.REQUEST_TYPE, Constants.AUTH_CHALLENGE_EXTERNAL);
                //send the challenge request
                out.println(respObject.toString());
                //read the response
                String response = in.readLine();
                if (response != null) {
                    //TODO: add these sysouts to debug logs.
                    System.out.println("Client: heard from IDM: " + response);
                    //read and remove session ID that has been set by RahasNym
                    JSONObject respObj = new JSONObject(new JSONTokener(response));
                    respObj.remove(Constants.SESSION_ID);
                    proofTxtField.setText(respObj.toString());

                } else {
                    JOptionPane.showMessageDialog(null, "Identity Token is null.", "IDT is null", JOptionPane.ERROR_MESSAGE);
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Could not connect to IDMM.", "IDMM connection failed.", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error obtaining IDT. Please check if IDMM is running.", "Error obtaining IDT.", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in creating the IDT request.", "Error obtaining IDT.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SubmitProofActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String loggedInSessionID = parentApplet.getParameter(Constants.SESSION_ID);

                HTTPClientRequest freeshipReq = new HTTPClientRequest();
                freeshipReq.setRequestType(SPClientConstants.RequestType.CREATE);
                freeshipReq.setRequestURI("http://localhost:8080/amazingshop/service/freeshipping");
                freeshipReq.setRequestHeader(SPClientConstants.LOGGED_IN_SESSION_ID, loggedInSessionID);
                JSONObject jsonReq = new JSONObject(proofTxtField.getText());
                jsonReq.put(Constants.USER_NAME, pseudonym);
                jsonReq.put(Constants.SESSION_ID, sessionIDAtSP);
                freeshipReq.setPayLoad(jsonReq.toString());
                int status = freeshipReq.execute();

                if (status == Constants.HTTP_ERROR_CODE) {
                    JOptionPane.showMessageDialog(null, "Error in verifying student ID. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    studentIDTxtField.setText("");
                    return;
                }
                String freeshipResponse = freeshipReq.getResponseString();
                JSONObject respObj = new JSONObject(new JSONTokener(freeshipResponse));
                String authResult = respObj.optString(Constants.VERIFICATION_RESULT);
                if (authResult.equals(Constants.AUTH_SUCCESS)) {
                    //forward the user to the portal page
                    parentApplet.getAppletContext().showDocument(new URL("http://localhost:8080/amazingshop/portal.jsp"));
                } else if (authResult.equals(Constants.AUTH_FAILURE)) {
                    JOptionPane.showMessageDialog(null, "Error in verifying student ID. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    studentIDTxtField.setText("");
                    challengeTxtField.setText("");
                    proofTxtField.setText("");
                    return;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in sending the sign up request.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in creating the sign up request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
