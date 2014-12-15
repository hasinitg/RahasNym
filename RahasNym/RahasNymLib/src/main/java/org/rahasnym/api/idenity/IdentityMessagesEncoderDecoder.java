package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 12:58 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This encodes/decodes major messages exchanged between different parties in the identity management system.
 * By separating this logic into a separate class, makes it easy to accommodate is any changes to the information
 * included in such messages.
 */
public class IdentityMessagesEncoderDecoder {

    public String encodeIdentityToken(IdentityToken IDT) throws JSONException {
        JSONObject IDTContent = new JSONObject();
        IDTContent.put(Constants.FROM, IDT.getPseudoNym());
        IDTContent.put(Constants.TO, IDT.getSpID());
        IDTContent.put(Constants.ATTRIBUTE_NAME, IDT.getAttributeName());
        IDTContent.put(Constants.IDENTITY_COMMITMENT, IDT.getIdentityCommitment().toString());
        IDTContent.put(Constants.CURRENT_TIMESTAMP, IDT.getCreationTimestamp().toString());
        IDTContent.put(Constants.EXPIRATION_TIMESTAMP, IDT.getExpirationTimeStamp().toString());

        //todo: encode biometric identity and single pseudonym certification if they are available in the IDT.

        //include the pedersen params
        JSONObject pedersenParams = new JSONObject();
        pedersenParams.put(Constants.P_PARAM, IDT.getPedersenParams().getP());
        pedersenParams.put(Constants.Q_PARAM, IDT.getPedersenParams().getQ());
        pedersenParams.put(Constants.G_PARAM, IDT.getPedersenParams().getG());
        pedersenParams.put(Constants.H_PARAM, IDT.getPedersenParams().getH());

        IDTContent.put(Constants.PEDERSEN_PARAMS, pedersenParams);
        //todo: attach the signature and the public cert

        JSONObject JSONIDT = new JSONObject();
        JSONIDT.put(Constants.IDT, JSONIDT);
        return JSONIDT.toString();
    }

    public IdentityToken decodeIdentityToken(String encodedIDT) throws JSONException, ParseException {
        IdentityToken identityToken = new IdentityToken();
        JSONObject root = new JSONObject(new JSONTokener(encodedIDT));
        JSONObject IDTJSON = root.optJSONObject(Constants.IDT);
        identityToken.setPseudoNym(IDTJSON.optString(Constants.FROM));
        identityToken.setSpID(IDTJSON.optString(Constants.TO));
        identityToken.setAttributeName(IDTJSON.optString(Constants.ATTRIBUTE_NAME));
        identityToken.setIdentityCommitment(new BigInteger(IDTJSON.optString(Constants.IDENTITY_COMMITMENT)));
        //read and set timestamp
        String currentTimestamp = IDTJSON.optString(Constants.CURRENT_TIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date creationTime = df.parse(currentTimestamp);
        identityToken.setCreationTimestamp(new Timestamp(creationTime.getTime()));

        String expirationTimestamp = IDTJSON.optString(Constants.EXPIRATION_TIMESTAMP);
        Date expTime = df.parse(expirationTimestamp);
        identityToken.setExpirationTimeStamp(new Timestamp(expTime.getTime()));

        JSONObject paramsObj = IDTJSON.optJSONObject(Constants.PEDERSEN_PARAMS);
        PedersenPublicParams params = new PedersenPublicParams();
        params.setP(new BigInteger(paramsObj.optString(Constants.P_PARAM)));
        params.setQ(new BigInteger(paramsObj.optString(Constants.Q_PARAM)));
        params.setG(new BigInteger(paramsObj.optString(Constants.G_PARAM)));
        params.setH(new BigInteger(paramsObj.optString(Constants.H_PARAM)));

        identityToken.setPedersenParams(params);

        return identityToken;
    }

    public PedersenPublicParams decodePedersenParams(String IDTString) throws JSONException {
        PedersenPublicParams params = new PedersenPublicParams();
        JSONObject root =  new JSONObject(new JSONTokener(IDTString));
        JSONObject IDTJSON = root.optJSONObject(Constants.IDT);
        JSONObject paramsObj = IDTJSON.optJSONObject(Constants.PEDERSEN_PARAMS);
        params.setP(new BigInteger(paramsObj.optString(Constants.P_PARAM)));
        params.setQ(new BigInteger(paramsObj.optString(Constants.Q_PARAM)));
        params.setG(new BigInteger(paramsObj.optString(Constants.G_PARAM)));
        params.setH(new BigInteger(paramsObj.optString(Constants.H_PARAM)));
        return params;
    }

    public String encodeIdentityProof() {
        return null;
    }

    public IdentityProof decodeIdentityProof(String encodedIDTProof) {
        return null;
    }

    public String encodeIDTRequest(IDTRequestMessage reqMsg) throws JSONException {
        JSONObject IDTReqContent = new JSONObject();
        IDTReqContent.put(Constants.ATTRIBUTE_NAME, reqMsg.getAttributeName());
        IDTReqContent.put(Constants.SECRET, reqMsg.getEncryptedSecret());
        IDTReqContent.put(Constants.PSEUDONYM_WITH_SP, reqMsg.getPseudonym());
        IDTReqContent.put(Constants.SP_IDENTITY, reqMsg.getSpIdentity());
        //todo: put single pseudonym req and bio id req

        JSONObject IDTReq = new JSONObject();
        IDTReq.put(Constants.IDT_REQUEST, IDTReq);
        return IDTReq.toString();
    }

    public IDTRequestMessage decodeIDTRequestMessage(String IDTReq) throws JSONException {
        IDTRequestMessage IDTReqMsg = new IDTRequestMessage();

        JSONObject root = new JSONObject(new JSONTokener(IDTReq));
        JSONObject IDTReqContent = root.optJSONObject(Constants.IDT_REQUEST);
        IDTReqMsg.setAttributeName(IDTReqContent.optString(Constants.ATTRIBUTE_NAME));
        IDTReqMsg.setEncryptedSecret(IDTReqContent.optString(Constants.SECRET));
        IDTReqMsg.setPseudonym(IDTReqContent.optString(Constants.PSEUDONYM_WITH_SP));
        IDTReqMsg.setSpIdentity(IDTReqContent.optString(Constants.SP_IDENTITY));

        return IDTReqMsg;
    }
}
