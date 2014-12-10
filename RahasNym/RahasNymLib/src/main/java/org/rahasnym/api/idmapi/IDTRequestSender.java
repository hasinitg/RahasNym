package org.rahasnym.api.idmapi;

import org.crypto.lib.CryptoLibConstants;
import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenCommitmentFactory;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.crypto.lib.util.CryptoUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.rahasnym.api.Constants;
import org.rahasnym.api.communication.policy.IDVPolicy;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 12:46 PM
 */
public class IDTRequestSender {

    public String requestIDT(IDVPolicy combinedPolicy, BigInteger secretBIG) throws CryptoAlgorithmException, NoSuchAlgorithmException, InvalidKeySpecException, JSONException {
        //todo: identify the attribute, corresponding IDP and access information about it, and send the IDT request.

        //for the moment, create the IDT here.
        PedersenCommitmentFactory factory = new PedersenCommitmentFactory();
        PedersenPublicParams params = factory.initialize();

        /*BigInteger secretBIG = CryptoUtil.getCommittableThruPBKDF(password, salt, params.getQ().bitLength() - 1,
                CryptoLibConstants.DEFAULT_PBKDF_ITERATIONS);*/

        String email = "hasinitg@gmail.com";
        BigInteger emailBIG = CryptoUtil.getCommittableThruHash(email, CryptoLibConstants.SECRET_BIT_LENGTH);
        PedersenCommitment commitment = factory.createCommitment(emailBIG, secretBIG);
        BigInteger commitmentBIG = commitment.getCommitment();

        JSONObject response = new JSONObject();
        response.append(Constants.IDT, commitmentBIG.toString());

        JSONObject pedersenParams = new JSONObject();
        pedersenParams.append(Constants.P_PARAM, params.getP().toString());
        pedersenParams.append(Constants.Q_PARAM, params.getQ().toString());
        pedersenParams.append(Constants.G_PARAM, params.getG().toString());
        pedersenParams.append(Constants.H_PARAM, params.getH().toString());
        response.append(Constants.PEDERSEN_PARAMS, pedersenParams);

        return response.toString();
    }
}
