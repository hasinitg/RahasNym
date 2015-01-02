package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 10:33 AM
 */

import org.rahasnym.api.RahasNymException;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is the class that provides methods for interfacing with SPClient and IDP.
 */
public class IDMMAPI {

    boolean listening = true;

    public void handleIDTRequests() {
        //listens to clients and handle them until the programme is closed
        try (
                ServerSocket serverSocket = new ServerSocket(IDMMConfig.getInstance().getIDMMPort());

        ) {
            while (listening) {
                //TODO: handle this with a thread pool
                new ClientRequestHandler(serverSocket.accept()).start();
            }
            return;
        } catch (IOException e) {
            //todo:log the error
            System.out.println("Error in listening on the socket: " + IDMMConfig.getInstance().getIDMMPort());
            //throw new RahasNymException(e.getMessage());
        }

    }

    public void stopIDMM(){
        listening = false;
    }

}
