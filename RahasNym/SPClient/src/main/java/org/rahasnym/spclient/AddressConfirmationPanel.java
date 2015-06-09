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
 * Date: 1/19/15
 * Time: 1:39 PM
 */
public class AddressConfirmationPanel extends JPanel {

    private String GET_IDT_COMMAND = "Get IDT";
    private String SUBMIT_IDT_COMMAND = "Submit IDT";
    private JApplet parentApplet = null;
    private JLabel shipAddrLabel = null;
    private JTextField shipAddrTxtField = null;
    private JButton getIDTBttn = null;
    private JButton submitIDTBttn = null;
    private String pseudonym;

    public AddressConfirmationPanel(JApplet applet) {
        shipAddrLabel = new JLabel("Shipping address IDT:");
        shipAddrTxtField = new JTextField("");
        parentApplet = applet;
        getIDTBttn = new JButton(GET_IDT_COMMAND);
        getIDTBttn.addActionListener(new GetIDTActionListener());
        submitIDTBttn = new JButton(SUBMIT_IDT_COMMAND);
        submitIDTBttn.addActionListener(new SubmitIDTActionListener());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(getIDTBttn);
        buttonPanel.add(submitIDTBttn);

        JPanel jPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        jPanel.add(shipAddrLabel);
        jPanel.add(shipAddrTxtField);
        jPanel.add(new JLabel(""));
        jPanel.add(buttonPanel);

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
                request.put(Constants.OPERATION, SPClientConstants.PURCHASE_CONFIRM_SHIPPING_ADDRESS);
                request.put(Constants.VERIFIER_POLICY, PolicyHolder.getInstance().getPolicy());
                request.put(Constants.PSEUDONYM_WITH_SP, pseudonym);
                //send the IDT request
                out.println(request.toString());
                //read the response
                String response = in.readLine();
                if (response != null) {
                    //TODO: add these sysouts to debug logs.
                    System.out.println("Client: heard from IDM: " + response);
                    shipAddrTxtField.setText(response);
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
                HTTPClientRequest addrConfReq = new HTTPClientRequest();
                addrConfReq.setRequestType(SPClientConstants.RequestType.CREATE);
                addrConfReq.setRequestURI("http://localhost:8080/amazingshop/service/address");
                JSONObject jsonReq = new JSONObject(shipAddrTxtField.getText());
                jsonReq.put(Constants.USER_NAME, pseudonym);

                addrConfReq.setPayLoad(jsonReq.toString());
                int status = addrConfReq.execute();

                if (status == Constants.HTTP_ERROR_CODE) {
                    JOptionPane.showMessageDialog(null, "Error in shipping address verification. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    shipAddrTxtField.setText("");
                    return;
                }
                String addrConfResponse = addrConfReq.getResponseString();
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                String authResult = encoderDecoder.decodeAuthResult(addrConfResponse);
                if (authResult.equals(Constants.AUTH_SUCCESS)) {
                    JSONObject jsonResp = new JSONObject(new JSONTokener(addrConfResponse));
                    //forward the user to signup successful page
                    parentApplet.getAppletContext().showDocument(new URL("http://localhost:8080/amazingshop/CCN_confirmation.jsp"));
                } else if (authResult.equals(Constants.AUTH_FAILURE)) {
                    JOptionPane.showMessageDialog(null, "Shipping address verification failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    shipAddrTxtField.setText("");
                    return;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in sending the shipping address verification request.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error in creating the shipping address verification request.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

}
