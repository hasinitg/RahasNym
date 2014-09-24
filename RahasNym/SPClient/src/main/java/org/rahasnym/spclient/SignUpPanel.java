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
public class SignUpPanel extends JPanel implements ActionListener {
    private static String POPULATE_COMMAND = "populate";
    private static String SUBMIT_COMMAND = "submit";
    private static String OPEN_FILE_COMMAND = "openFile";
    JLabel nameLabel;
    JTextField nameText;

    public SignUpPanel() {
        super(new BorderLayout());
        nameLabel = new JLabel("Name is: ");

        nameText = new JTextField("");

        JButton populateButton = new JButton("PopulateB");
        populateButton.setActionCommand(POPULATE_COMMAND);
        populateButton.addActionListener(this);

        JButton sendButton = new JButton("SubmitB");
        sendButton.setActionCommand(SUBMIT_COMMAND);
        sendButton.addActionListener(this);

        JButton openFileButton = new JButton("Open Policy FileB");
        openFileButton.setActionCommand(OPEN_FILE_COMMAND);
        openFileButton.addActionListener(this);

        JPanel jPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        jPanel.add(nameLabel);
        jPanel.add(nameText);
        jPanel.add(new JLabel(""));
        jPanel.add(populateButton);
        jPanel.add(sendButton);
        jPanel.add(openFileButton);
        //jPanel.setLayout(new GridLayout(2, 3, 10, 10));
        //jPanel.setPreferredSize(new Dimension(100, 100));
        add(jPanel, BorderLayout.CENTER);
    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        SPClientIDMProtocol clientIDMProtocol = new SPClientIDMProtocol();
        String command = e.getActionCommand();
        //nameText.setText("Please work..");

        if (POPULATE_COMMAND.equals(command)) {
            //nameLabel.setText("Changed..");
            nameText.setText("Populate clicked..");
            clientIDMProtocol.obtainIdentityToken();
        } else if (SUBMIT_COMMAND.equals(command)) {
            clientIDMProtocol.connectToRemoteHost();
            nameText.setText("Submit clicked..");
        } else if (OPEN_FILE_COMMAND.equals(command)) {
            clientIDMProtocol.openPolicyFile();
            nameText.setText("Open File clicked..");
        }
    }
}
