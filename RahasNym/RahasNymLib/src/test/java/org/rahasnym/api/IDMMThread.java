package org.rahasnym.api;

import org.rahasnym.api.idmapi.IDMMAPI;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/16/14
 * Time: 4:04 PM
 */
public class IDMMThread extends Thread {
    @Override
    public void run() {
        IDMMAPI idmmapi = new IDMMAPI();
        idmmapi.handleIDTRequests();
    }
}
