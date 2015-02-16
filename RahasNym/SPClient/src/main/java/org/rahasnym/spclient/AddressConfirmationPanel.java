package org.rahasnym.spclient;

import org.rahasnym.api.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            String pseudonym = parentApplet.getParameter(Constants.USER_NAME);
            System.out.println(pseudonym);
        }
    }

    private class SubmitIDTActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

}
