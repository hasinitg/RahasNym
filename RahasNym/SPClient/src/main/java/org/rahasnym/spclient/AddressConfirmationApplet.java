package org.rahasnym.spclient;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/19/15
 * Time: 1:30 PM
 */
public class AddressConfirmationApplet extends JApplet {

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
        AddressConfirmationPanel addressConfirmationPanel = new AddressConfirmationPanel(this);
        addressConfirmationPanel.setOpaque(true);
        setContentPane(addressConfirmationPanel);
    }
}
