package org.rahasnym.spclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/17/14
 * Time: 2:41 PM
 */

public class Tester extends JFrame {
    private JButton save = new JButton("Save");

    public Tester() {
        /*JPanel p = new JPanel();
        save.addActionListener(new SaveL());
        p.add(save);
        p.setLayout(new GridLayout(2, 1));

        add(p);*/
        SignUpPanel p = new SignUpPanel();
        add(p);
    }

    public static void main(String[] args) {
        Tester abc = new Tester();
        abc.pack();
        abc.setVisible(true);
    }

}

class SaveL implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Hello"); // nothing happens

    }
}
