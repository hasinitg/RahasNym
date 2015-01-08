package org.rahasnym.api;

import org.rahasnym.api.idmapi.IDMMAPI;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/16/14
 * Time: 4:04 PM
 */
public class IDMMThread extends Thread {
    private boolean stopped = false;
    IDMMAPI idmmapi = null;

    @Override
    public void run() {
        idmmapi = new IDMMAPI();
        try {
            idmmapi.handleIDTRequests();
        } catch (RahasNymException e) {
            e.printStackTrace();
        }
    }

    public void stopMe() throws IOException {
        idmmapi.stopIDMM();
    }
}
