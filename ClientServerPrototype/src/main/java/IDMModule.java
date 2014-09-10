import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/9/14
 * Time: 10:06 AM
 */
public class IDMModule {

    public void init() {
        //listens to clients and handle them until the programme is closed
        boolean listening = true;
        try (
                ServerSocket serverSocket = new ServerSocket(Constants.IDM_MODULE_PORT);

        ) {
            while (listening) {
                //TODO: handle this with a thread pool
                new IDMThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Error in listening on the socket: " + Constants.IDM_MODULE_PORT);
            e.printStackTrace();
        }

    }

}
