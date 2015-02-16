package org.rahasnym.spclient;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 2/10/15
 * Time: 12:17 AM
 */
public class SIDConfirmationApplet extends JApplet {
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
        SIDConfirmationPanel sidConfirmationPanel = new SIDConfirmationPanel(this);
        sidConfirmationPanel.setOpaque(true);
        setContentPane(sidConfirmationPanel);
    }

}
