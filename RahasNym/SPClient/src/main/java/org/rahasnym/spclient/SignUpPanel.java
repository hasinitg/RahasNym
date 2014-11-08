package org.rahasnym.spclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/15/14
 * Time: 9:56 AM
 */
public class SignUpPanel extends JPanel {
    private static String POPULATE_COMMAND = "Populate email token";
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

    public SignUpPanel() {
        super(new BorderLayout());
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

    class PopulateActionListener implements ActionListener{
        SPClientIDMProtocol clientIDMProtocol = new SPClientIDMProtocol();
        @Override
        public void actionPerformed(ActionEvent e) {
            nameText.setText("Populate clicked..");
            clientIDMProtocol.obtainIdentityToken();
        }
    }

    class SubmitActionListener implements ActionListener{
        SPClientIDMProtocol clientIDMProtocol = new SPClientIDMProtocol();
        @Override
        public void actionPerformed(ActionEvent e) {
            nameText.setText("Submit clicked..");
            clientIDMProtocol.connectToRemoteHost();
        }
    }
}