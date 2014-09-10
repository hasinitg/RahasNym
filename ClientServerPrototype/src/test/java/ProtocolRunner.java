/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/9/14
 * Time: 2:00 PM
 */
public class ProtocolRunner extends Thread {
    IDMModule idmModule = null;
    SPClient spClient = null;

    public ProtocolRunner(IDMModule module) {
        super("ProtocolRunner");
        this.idmModule = module;
    }

    public ProtocolRunner(SPClient client) {
        super("ProtocolRunner");
        this.spClient = client;
    }

    public void run() {
        if (idmModule != null) {
            idmModule.init();
        } else {
            spClient.performIdentityProof();
        }

    }

}
