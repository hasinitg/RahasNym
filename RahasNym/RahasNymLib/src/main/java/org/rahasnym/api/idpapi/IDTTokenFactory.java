package org.rahasnym.api.idpapi;

import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.rahasnym.api.idenity.IdentityToken;

import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:04 PM
 */
public class IDTTokenFactory {

    //create idt
    public IdentityToken createIdentityToken() throws CryptoAlgorithmException, NoSuchAlgorithmException {
        //initialize pedersen commitment factory
        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        pedersenCommitmentFactory.initialize(IDPConfig.getInstance().getPedersenPublicParams());

        return null;

    }

    //encode idt

    //decode idt
}
