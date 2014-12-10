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

    public void handleIDTRequests() throws RahasNymException {
        //listens to clients and handle them until the programme is closed
        boolean listening = true;
        try (
                ServerSocket serverSocket = new ServerSocket(IDMMConfig.getIDMMPort());

        ) {
            while (listening) {
                //TODO: handle this with a thread pool
                new ClientRequestHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            //todo:log the error
            System.out.println("Error in listening on the socket: " + IDMMConfig.getIDMMPort());
            throw new RahasNymException(e.getMessage());
        }

    }

}
