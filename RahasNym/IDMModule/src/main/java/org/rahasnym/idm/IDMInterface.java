package org.rahasnym.idm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/29/14
 * Time: 8:08 AM
 */

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This implements the interface provided by IDMModule for client applications of different service providers
 * to interact with it for requesting tokens and carrying out identity proofs.
 */
public class IDMInterface {
    public void init() {
        //listens to clients and handle them until the programme is closed
        boolean listening = true;
        try (
                ServerSocket serverSocket = new ServerSocket(IDMConstants.IDM_MODULE_PORT);

        ) {
            while (listening) {
                //TODO: handle this with a thread pool
                new IDMModule(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Error in listening on the socket: " + IDMConstants.IDM_MODULE_PORT);
            e.printStackTrace();
        }

    }

}
