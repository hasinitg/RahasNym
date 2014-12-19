package org.rahasnym.api.idpapi;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.rahasnym.api.idenity.IDTRequestMessage;
import org.rahasnym.api.idenity.IdentityToken;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:04 PM
 */
public class IDTTokenFactory {

    //create idt
    public IdentityToken createIdentityToken(IDTRequestMessage IDTReqMessage, String userName) throws CryptoAlgorithmException, NoSuchAlgorithmException {
        //initialize pedersen commitment factory
        PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
        pedersenCommitmentFactory.initialize(IDPConfig.getInstance().getPedersenPublicParams());

        //TODO: decrypt the secret and obtain the big integer
        String encryptedSecret = IDTReqMessage.getEncryptedSecret();
        //todo: decrypt the secret
        BigInteger secretBIG = new BigInteger(encryptedSecret);
        //TODO: get the user's attribute value from the AttributeFinder given the user name and the attribute name
        String email = "hasinitg@gmail.com";
        BigInteger emailBIG = CryptoUtil.getCommittableThruHash(email, CryptoLibConstants.SECRET_BIT_LENGTH);
        //System.out.println("emailBIG at IDP: " + emailBIG);
        PedersenCommitment commitment = pedersenCommitmentFactory.createCommitment(emailBIG, secretBIG);
        BigInteger commitmentBIG = commitment.getCommitment();

        IdentityToken IDT = new IdentityToken();
        IDT.setPseudoNym(IDTReqMessage.getPseudonym());
        IDT.setSpID(IDTReqMessage.getSpIdentity());
        IDT.setAttributeName(IDTReqMessage.getAttributeName());
        IDT.setIdentityCommitment(commitmentBIG);

        //create current timestamp and expiration timestamp
        Date now = new Date();
        Timestamp currentTimestamp = new Timestamp(now.getTime());
        IDT.setCreationTimestamp(currentTimestamp);
        Timestamp expirationTimestamp = new Timestamp(now.getTime() + IDPConfig.getInstance().getIDTTimeToLive());
        IDT.setExpirationTimeStamp(expirationTimestamp);

        //include pedersen parameters
        IDT.setPedersenParams(IDPConfig.getInstance().getPedersenPublicParams());

        //todo: attach the signature and ref to public cert
        //todo: if single pseudonym and biometric identity attached, do accordingly.
        return IDT;
    }

    //encode idt

    //decode idt
}
