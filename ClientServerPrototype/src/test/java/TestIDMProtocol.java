import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/9/14
 * Time: 11:55 AM
 */
public class TestIDMProtocol {
    public static void main(String[] args) {
        ProtocolRunner module = new ProtocolRunner(new IDMModule());
        module.start();

        ProtocolRunner client = new ProtocolRunner(new SPClient());
        client.start();

    }
}
