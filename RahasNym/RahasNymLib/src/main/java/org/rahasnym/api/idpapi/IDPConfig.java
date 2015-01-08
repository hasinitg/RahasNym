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
import org.rahasnym.api.Constants;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * This is a singleton class holds the configuration information related to the IDP such as: Pedersen configuration
 * parameters, RSA public key, private key as strings.
 * An instance of this needs to be created at the startup of the IDP.
 */
public class IDPConfig {

    private static PedersenPublicParams pedersenPublicParams;
    private static PrivateKey RSAPrivateKey;
    private static String certificateAlias;
    //private static PublicKey RSAPublicKey;
    private static long IDTTimeToLive;

    private static volatile IDPConfig idpConfig = null;

    private IDPConfig() throws CryptoAlgorithmException, NoSuchAlgorithmException {
        //todo: give a call back handler to set the following params which are for the moment initialized in the constructor.
        //handle the exceptions properly.
        pedersenPublicParams = new PedersenCommitmentFactory().initialize();
        IDTTimeToLive = Constants.DEFAULT_IDT_TTL;
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

    public PrivateKey getRSAPrivateKey() {
        return RSAPrivateKey;
    }

    /*public PublicKey getRSAPublicKey() {
        return RSAPublicKey;
    }*/

    public void setRSAPrivateKey(PrivateKey privateKey){
        RSAPrivateKey =  privateKey;
    }

    /*public void setRSAPublicKey(PublicKey publicKey){
       RSAPublicKey = publicKey;
    }*/

    public long getIDTTimeToLive() {
        return IDTTimeToLive;
    }

    public void setIDTTimeToLive(long IDTTimeToLive) {
        IDPConfig.IDTTimeToLive = IDTTimeToLive;
    }

    public static String getCertificateAlias() {
        return certificateAlias;
    }

    public static void setCertificateAlias(String certificateAlias) {
        IDPConfig.certificateAlias = certificateAlias;
    }
}
