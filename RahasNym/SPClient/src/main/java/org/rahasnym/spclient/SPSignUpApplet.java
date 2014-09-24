package org.rahasnym.spclient;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/15/14
 * Time: 9:56 AM
 */
public class SPSignUpApplet extends JApplet {

    @Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    createUI();
                }
            });
        } catch (InterruptedException e) {
            System.out.println("GUI creation didn't successful.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("GIU creation didn't successful.");
            e.printStackTrace();
        }
    }

    private void createUI() {
        System.out.println("create UI called.");
        SignUpPanel signUpPanel = new SignUpPanel();
        signUpPanel.setOpaque(true);
        setContentPane(signUpPanel);
    }

    /*public void main(String[] args) {
        createUI();
    }*/

}
