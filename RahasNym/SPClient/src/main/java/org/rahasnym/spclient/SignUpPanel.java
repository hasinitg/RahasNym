package org.rahasnym.spclient;

import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.*;
import org.rahasnym.api.Constants;
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
import java.net.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/15/14
 * Time: 9:56 AM
 */
public class SignUpPanel extends JPanel {
    private static String POPULATE_COMMAND = "Get Email IDT";
    private static String SUBMIT_COMMAND = "Submit";
    //private static String OPEN_FILE_COMMAND = "openFile";
    JLabel nameLabel;
    JTextField nameText;
    JLabel emailLabel;
    JTextField emailText;
    JLabel passLabel;
    JPasswordField passText;
    JLabel repassLabel;
    JPasswordField repassText;
    JApplet applet;

    public SignUpPanel(JApplet applet) {
        super(new BorderLayout());
        this.applet = applet;
        nameLabel = new JLabel("User Name: ");
        nameText = new JTextField("");
        emailLabel = new JLabel("Email");
        emailText = new JTextField("");
        passLabel = new JLabel("Password");
        passText = new JPasswordField("");
        repassLabel = new JLabel("Re-type Password");
        repassText = new JPasswordField("");

        JButton populateButton = new JButton(POPULATE_COMMAND);
        //populateButton.setActionCommand(POPULATE_COMMAND);
        populateButton.addActionListener(new PopulateActionListener());

        JButton sendButton = new JButton(SUBMIT_COMMAND);
        //sendButton.setActionCommand(SUBMIT_COMMAND);
        sendButton.addActionListener(new SubmitActionListener());

        /*JButton openFileButton = new JButton("Open Policy FileB");
        //openFileButton.setActionCommand(OPEN_FILE_COMMAND);
        openFileButton.addActionListener(this);*/

        JPanel jPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        jPanel.add(nameLabel);
        jPanel.add(nameText);
        //jPanel.add(new JLabel(""));
        jPanel.add(emailLabel);
        jPanel.add(emailText);
        jPanel.add(new JLabel(""));
        jPanel.add(populateButton);
        jPanel.add(passLabel);
        jPanel.add(passText);
        //jPanel.add(new Label(""));
        jPanel.add(repassLabel);
        jPanel.add(repassText);
        jPanel.add(new Label(""));
        jPanel.add(sendButton);
        //jPanel.setLayout(new GridLayout(2, 3, 10, 10));
        //jPanel.setPreferredSize(new Dimension(100, 100));
        add(jPanel, BorderLayout.CENTER);
    }

    class PopulateActionListener implements ActionListener {
        SPClientIDMProtocol clientIDMProtocol = new SPClientIDMProtocol();

        @Override
        public void actionPerformed(ActionEvent e) {
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
                request.put(Constants.OPERATION, SPClientConstants.SIGN_UP_OPERATION);
                request.put(Constants.VERIFIER_POLICY, PolicyHolder.getInstance().getPolicy());
                request.put(Constants.PSEUDONYM_WITH_SP, nameText.getText());
                //send the IDT request
                out.println(request.toString());
                //read the response
                String response = in.readLine();
                if (response != null) {
                    //TODO: add these sysouts to debug logs.
                    System.out.println("Client: heard from IDM: " + response);
                    emailText.setText(response);
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

    class SubmitActionListener implements ActionListener {
        SPClientIDMProtocol clientIDMProtocol = new SPClientIDMProtocol();

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                HTTPClientRequest signupReq = new HTTPClientRequest();
                signupReq.setRequestType(SPClientConstants.RequestType.CREATE);
                signupReq.setRequestURI("http://localhost:8080/amazingshop/service/signup");
                JSONObject jsonReq = new JSONObject(emailText.getText());
                jsonReq.put(Constants.USER_NAME, nameText.getText());

                //check if password is correctly repeated.
                char[] pass = passText.getPassword();
                char[] repass = repassText.getPassword();
                if ((pass != null) && (repass != null) && (Arrays.equals(pass, repass))) {
                    jsonReq.put(SPClientConstants.PASSWORD, new String(pass));
                } else {
                    JOptionPane.showMessageDialog(null, "Error in password. Please re-enter.", "Error", JOptionPane.ERROR_MESSAGE);
                    passText.setText("");
                    repassText.setText("");
                    return;
                }
                Arrays.fill(pass, '0');
                Arrays.fill(repass, '0');
                //passText.setEchoChar('0');
                //repassText.setEchoChar('0');

                signupReq.setPayLoad(jsonReq.toString());
                int status = signupReq.execute();

                if (status == Constants.HTTP_ERROR_CODE) {
                    JOptionPane.showMessageDialog(null, "Error in signing up. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    nameText.setText("");
                    emailText.setText("");
                    passText.setText("");
                    repassText.setText("");
                    return;
                }
                String signUpResponse = signupReq.getResponseString();
                IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
                String authResult = encoderDecoder.decodeAuthResult(signUpResponse);
                String sessionID = null;
                if (authResult.equals(Constants.AUTH_SUCCESS)) {
                    JSONObject jsonResp = new JSONObject(new JSONTokener(signUpResponse));
                    sessionID = jsonResp.optString(Constants.SESSION_ID);
                    //set the returned session id in the browser cookie
                    JSObject.getWindow(applet).eval("document.cookie ='" + Constants.SESSION_ID + "=" + sessionID + "';");
                    //forward the user to signup successful page
                    applet.getAppletContext().showDocument(new URL("http://localhost:8080/amazingshop/signup_confirmation_success.jsp"));
                } else if (authResult.equals(Constants.AUTH_FAILURE)) {
                    JOptionPane.showMessageDialog(null, "Error in signing up. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    nameText.setText("");
                    emailText.setText("");
                    passText.setText("");
                    repassText.setText("");
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