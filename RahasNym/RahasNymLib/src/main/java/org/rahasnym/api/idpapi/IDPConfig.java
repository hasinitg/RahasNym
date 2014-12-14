package org.rahasnym.api.idpapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:07 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.security.NoSuchAlgorithmException;

/**
 * This is a singleton class holds the configuration information related to the IDP such as: Pedersen configuration
 * parameters, RSA public key, private key as strings.
 * An instance of this needs to be created at the startup of the IDP.
 */
public class IDPConfig {

    private static PedersenPublicParams pedersenPublicParams;
    private static String RSAPrivateKey;
    private static String RSAPublicKey;

    private static volatile IDPConfig idpConfig = null;

    private IDPConfig() throws CryptoAlgorithmException, NoSuchAlgorithmException {
        pedersenPublicParams = new PedersenCommitmentFactory().initialize();
        //initialize other configs as well.
    }

    public static IDPConfig getInstance() throws NoSuchAlgorithmException, CryptoAlgorithmException {
        if(idpConfig == null){
            synchronized (IDPConfig.class){
                if(idpConfig == null){
                    idpConfig = new IDPConfig();
                }
            }
        }
        return idpConfig;
    }

    public PedersenPublicParams getPedersenPublicParams() {
        return pedersenPublicParams;
    }

    public String getRSAPrivateKey() {
        return RSAPrivateKey;
    }

    public String getRSAPublicKey() {
        return RSAPublicKey;
    }
}
