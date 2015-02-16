package org.rahasnym.api.idpapi;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.PKC.RSA.SignerVerifier;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.rahasnym.api.Constants;
import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.idenity.*;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 6:04 PM
 */
public class IDTTokenFactory {

    //create idt
    public IdentityToken createIdentityToken(IDTRequestMessage IDTReqMessage, String userName)
            throws RahasNymException {
        try {
            //initialize pedersen commitment factory
            PedersenCommitmentFactory pedersenCommitmentFactory = new PedersenCommitmentFactory();
            pedersenCommitmentFactory.initialize(IDPConfig.getInstance().getPedersenPublicParams());

            //TODO: decrypt the secret and obtain the big integer
            String encryptedSecret = IDTReqMessage.getEncryptedSecret();
            //todo: decrypt the secret
            BigInteger secretBIG = new BigInteger(encryptedSecret);
            //TODO: get the user's attribute value from the AttributeFinder given the user name and the attribute name
            //String email = "hasinitg@gmail.com";
            String attributeName = IDTReqMessage.getAttributeName();
            String attributeValue = AttributeCallBackManager.getAttributeValue(userName, attributeName);
            BigInteger emailBIG = CryptoUtil.getCommittableThruHash(attributeValue, CryptoLibConstants.SECRET_BIT_LENGTH);
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
            IDT.setCreationTimestamp(reformatTimestamp(currentTimestamp));

            Timestamp expirationTimestamp = new Timestamp(now.getTime() + IDPConfig.getInstance().getIDTTimeToLive());
            IDT.setExpirationTimeStamp(reformatTimestamp(expirationTimestamp));

            //include pedersen parameters
            PedersenPublicParams pedersenParams = IDPConfig.getInstance().getPedersenPublicParams();
            IDT.setPedersenParams(pedersenParams);

            IdentityMessagesEncoderDecoder encoderDecoder = new IdentityMessagesEncoderDecoder();
            String concatenatedInfo = encoderDecoder.getConcatenatedInfoFromIDT(IDT);

            SignerVerifier signer = new SignerVerifier();
            String signature = signer.signMessageAsString(concatenatedInfo, IDPConfig.getInstance().getRSAPrivateKey());
            IDT.setSignature(signature);
            IDT.setPublicCertAlias(IDPConfig.getInstance().getCertificateAlias());
            //todo: if single pseudonym and biometric identity attached, do accordingly.
            return IDT;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in creating the identity commitment.");
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in creating the Pedersen commitment.");
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in signing the identity token.");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in signing the identity token.");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RahasNymException("Error in parsing the timestamp.");
        }
    }

    private Timestamp reformatTimestamp(Timestamp ts) throws ParseException {
        String timestampInStr = ts.toString();
        SimpleDateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);
        Date parsedDate = df.parse(timestampInStr);
        return new Timestamp(parsedDate.getTime());
    }
}
