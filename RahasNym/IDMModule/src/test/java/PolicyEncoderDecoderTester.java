import org.rahasnym.idm.IDMException;
import org.rahasnym.idm.IDVPolicy;
import org.rahasnym.idm.PolicyEncoderDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/6/14
 * Time: 1:30 PM
 */
public class PolicyEncoderDecoderTester {
    public static void main(String[] args) {
        PolicyEncoderDecoder policyReader = new PolicyEncoderDecoder();
        try {
            IDVPolicy idvPolicy = policyReader.readPolicyFromFile("/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/server_policy.xml");
        } catch (IDMException e) {
            e.printStackTrace();
        }
    }
}
