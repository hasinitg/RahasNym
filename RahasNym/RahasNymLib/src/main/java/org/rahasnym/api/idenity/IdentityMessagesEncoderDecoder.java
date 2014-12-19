package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 12:58 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenCommitment;
import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.crypto.lib.zero.knowledge.proof.PedersenCommitmentProof;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;
import org.rahasnym.api.verifierapi.ProofInfo;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This encodes/decodes major messages exchanged between different parties in the identity management system.
 * By separating this logic into a separate class, makes it easy to accommodate is any changes to the information
 * included in such messages.
 */
public class IdentityMessagesEncoderDecoder {

    public String encodeIdentityToken(IdentityToken IDT) throws JSONException {
        JSONObject IDTContent = createIdentityTokenContent(IDT);
        JSONObject JSONIDT = new JSONObject();
        JSONIDT.put(Constants.IDT, IDTContent);
        return JSONIDT.toString();
    }

    private JSONObject createIdentityTokenContent(IdentityToken IDT) throws JSONException {
        JSONObject IDTContent = new JSONObject();
        IDTContent.put(Constants.FROM, IDT.getPseudoNym());
        IDTContent.put(Constants.TO, IDT.getSpID());
        IDTContent.put(Constants.ATTRIBUTE_NAME, IDT.getAttributeName());
        IDTContent.put(Constants.IDENTITY_COMMITMENT, IDT.getIdentityCommitment().toString());
        IDTContent.put(Constants.CURRENT_TIMESTAMP, IDT.getCreationTimestamp().toString());
        IDTContent.put(Constants.EXPIRATION_TIMESTAMP, IDT.getExpirationTimeStamp().toString());

        //todo: encode biometric identity and single pseudonym certification if they are available in the IDT.
        //todo: attach the signature and the public cert
        //include the pedersen params
        JSONObject pedersenParams = new JSONObject();
        pedersenParams.put(Constants.P_PARAM, IDT.getPedersenParams().getP().toString());
        pedersenParams.put(Constants.Q_PARAM, IDT.getPedersenParams().getQ().toString());
        pedersenParams.put(Constants.G_PARAM, IDT.getPedersenParams().getG().toString());
        pedersenParams.put(Constants.H_PARAM, IDT.getPedersenParams().getH().toString());
        IDTContent.put(Constants.PEDERSEN_PARAMS, pedersenParams);
        return IDTContent;
    }

    public IdentityToken decodeIdentityToken(String encodedIDT) throws JSONException, ParseException {
        JSONObject root = new JSONObject(new JSONTokener(encodedIDT));
        JSONObject IDTJSON = root.optJSONObject(Constants.IDT);

        return decodeIdentityTokenContent(IDTJSON);
    }

    public IdentityToken decodeIdentityTokenContent(JSONObject IDTContent) throws ParseException {
        IdentityToken identityToken = new IdentityToken();

        identityToken.setPseudoNym(IDTContent.optString(Constants.FROM));
        identityToken.setSpID(IDTContent.optString(Constants.TO));
        identityToken.setAttributeName(IDTContent.optString(Constants.ATTRIBUTE_NAME));
        identityToken.setIdentityCommitment(new BigInteger(IDTContent.optString(Constants.IDENTITY_COMMITMENT)));
        //read and set timestamp
        String currentTimestamp = IDTContent.optString(Constants.CURRENT_TIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date creationTime = df.parse(currentTimestamp);
        identityToken.setCreationTimestamp(new Timestamp(creationTime.getTime()));

        String expirationTimestamp = IDTContent.optString(Constants.EXPIRATION_TIMESTAMP);
        Date expTime = df.parse(expirationTimestamp);
        identityToken.setExpirationTimeStamp(new Timestamp(expTime.getTime()));

        JSONObject paramsObj = IDTContent.optJSONObject(Constants.PEDERSEN_PARAMS);
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
        JSONObject root = new JSONObject(new JSONTokener(IDTString));
        JSONObject IDTJSON = root.optJSONObject(Constants.IDT);
        JSONObject paramsObj = IDTJSON.optJSONObject(Constants.PEDERSEN_PARAMS);
        params.setP(new BigInteger(paramsObj.optString(Constants.P_PARAM)));
        params.setQ(new BigInteger(paramsObj.optString(Constants.Q_PARAM)));
        params.setG(new BigInteger(paramsObj.optString(Constants.G_PARAM)));
        params.setH(new BigInteger(paramsObj.optString(Constants.H_PARAM)));
        return params;
    }

    public String encodeIDTRequest(IDTRequestMessage reqMsg) throws JSONException {
        JSONObject IDTReqContent = new JSONObject();
        IDTReqContent.put(Constants.ATTRIBUTE_NAME, reqMsg.getAttributeName());
        IDTReqContent.put(Constants.SECRET, reqMsg.getEncryptedSecret());
        IDTReqContent.put(Constants.PSEUDONYM_WITH_SP, reqMsg.getPseudonym());
        IDTReqContent.put(Constants.SP_IDENTITY, reqMsg.getSpIdentity());
        //todo: put single pseudonym req and bio id req

        JSONObject IDTReq = new JSONObject();
        IDTReq.put(Constants.IDT_REQUEST, IDTReqContent);
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


    public JSONObject encodeIdentityProofContent(IdentityProof proof) throws JSONException {
        JSONObject proofContent = new JSONObject();
        proofContent.put(Constants.PROOF_TYPE, proof.getProofType());

        if (Constants.ZKP_I.equals(proof.getProofType())) {

            if (proof.getHelperCommitment() != null) {
                proofContent.put(Constants.HELPER_COMMITMENT, proof.getHelperCommitment().toString());
            }
            if (proof.getProof() != null) {
                proofContent.put(Constants.U_VALUE, proof.getProof().getU().toString());
                proofContent.put(Constants.V_VALUE, proof.getProof().getV().toString());
            }
        } else if (Constants.ZKP_NI.equals(proof.getProofType())) {

            if (proof.getHelperCommitments() != null && proof.getHelperCommitments().size() != 0) {
                JSONArray helperCommitmentsArray = new JSONArray();
                List<BigInteger> helperCommitments = proof.getHelperCommitments();
                helperCommitmentsArray.put(helperCommitments.get(0).toString());
                proofContent.put(Constants.HELPER_COMMITMENTS, helperCommitmentsArray);
                List<PedersenCommitmentProof> proofs = proof.getProofs();
                JSONArray uValues = new JSONArray();
                JSONArray vValues = new JSONArray();
                for (PedersenCommitmentProof pedersenCommitmentProof : proofs) {
                    uValues.put(pedersenCommitmentProof.getU().toString());
                    vValues.put(pedersenCommitmentProof.getV().toString());
                }
                proofContent.put(Constants.U_VALUES, uValues);
                proofContent.put(Constants.V_VALUES, vValues);
                proofContent.put(Constants.TIMESTAMP_AT_PROOF_CREATION, proof.getTimestampAtProofCreation());

            }
        }
        return proofContent;
    }

    public IdentityProof decodeIdentityProofContent(JSONObject proofContent) throws JSONException {
        IdentityProof proof = new IdentityProof();
        String helperCommitmentString = proofContent.optString(Constants.HELPER_COMMITMENT);
        if (helperCommitmentString != null) {
            BigInteger helperCommitment = new BigInteger(helperCommitmentString);
            proof.addHelperCommitment(helperCommitment);
        }
        JSONArray helperCommitmentsString = proofContent.optJSONArray(Constants.HELPER_COMMITMENTS);
        if (helperCommitmentsString != null) {
            JSONArray uValuesString = proofContent.optJSONArray(Constants.U_VALUES);
            JSONArray vValuesString = proofContent.optJSONArray(Constants.V_VALUES);
            for (int i = 0; i < 3; i++) {
                proof.addHelperCommitment(new BigInteger(helperCommitmentsString.getString(i)));
                PedersenCommitmentProof idProof = new PedersenCommitmentProof();
                idProof.setU(new BigInteger(uValuesString.getString(i)));
                idProof.setV(new BigInteger(vValuesString.getString(i)));
                proof.addProof(idProof);
            }

        }
        return proof;
    }

    public String encodeIdentityProof() {
        return null;
    }

    public IdentityProof decodeIdentityProof(String encodedIDTProof) {
        return null;
    }

    public String createIDTResponseByIDMM(String identityToken, IdentityProof identityProof) throws JSONException {
        JSONObject responseJSON = new JSONObject(identityToken);
        JSONObject proofJSON = encodeIdentityProofContent(identityProof);
        responseJSON.put(Constants.PROOF, proofJSON);
        if (Constants.ZKP_I.equals(identityProof.getProofType())) {
            responseJSON.put(Constants.REQUEST_TYPE, Constants.REQ_ZKP_I);
        } else if (Constants.ZKP_NI.equals(identityProof.getProofType())) {
            responseJSON.put(Constants.REQUEST_TYPE, Constants.REQ_ZKP_NI);
        } else if (Constants.ZKP_NI_S.equals(identityProof.getProofType())) {
            responseJSON.put(Constants.REQUEST_TYPE, Constants.REQ_ZKP_NI_S);
        }
        return responseJSON.toString();
    }

    public String createChallengeResponseByIDMM(String sessionID, IdentityProof proof) throws JSONException {
        JSONObject proofContent = encodeIdentityProofContent(proof);
        JSONObject challengeResponseMessage = new JSONObject(proofContent.toString());
        challengeResponseMessage.put(Constants.REQUEST_TYPE, Constants.AUTH_CHALLENGE_RESPONSE);
        challengeResponseMessage.put(Constants.SESSION_ID, sessionID);
        //challengeResponseMessage.put(Constants.U_VALUE, proof.getProof().getU().toString());
        //challengeResponseMessage.put(Constants.V_VALUE, proof.getProof().getV().toString());
        return challengeResponseMessage.toString();
    }

    public IdentityProof decodeChallengeResponse(JSONObject challengeResponse) {
        IdentityProof proof = new IdentityProof();
        PedersenCommitmentProof pedersenCommitmentProof = new PedersenCommitmentProof();
        pedersenCommitmentProof.setU(new BigInteger(challengeResponse.optString(Constants.U_VALUE)));
        pedersenCommitmentProof.setV(new BigInteger(challengeResponse.optString(Constants.V_VALUE)));
        proof.addProof(pedersenCommitmentProof);
        return proof;
    }

    public String encodeChallengeMessage(ProofInfo proofInfo) throws JSONException {
        JSONObject challengeContent = new JSONObject();
        challengeContent.put(Constants.REQUEST_TYPE, Constants.AUTH_CHALLENGE);
        challengeContent.put(Constants.SESSION_ID, proofInfo.getSessionID());
        challengeContent.put(Constants.CHALLENGE, proofInfo.getChallengeValue().toString());
        return challengeContent.toString();
    }

    public ProofInfo decodeChallengeMessage(JSONObject challengeMessage) {
        String challenge = challengeMessage.optString(Constants.CHALLENGE);
        ProofInfo proofInfo = new ProofInfo();
        proofInfo.setChallengeValue(new BigInteger(challenge));
        String sessionId = challengeMessage.optString(Constants.SESSION_ID);
        proofInfo.setSessionID(sessionId);
        return proofInfo;
    }

    public String createAuthResultMessage(boolean authResult) throws JSONException {
        JSONObject authResponse = new JSONObject();
        authResponse.put(Constants.REQUEST_TYPE, Constants.AUTH_RESULT);
        if (authResult) {
            authResponse.put(Constants.VERIFICATION_RESULT, Constants.AUTH_SUCCESS);
        } else {
            authResponse.put(Constants.VERIFICATION_RESULT, Constants.AUTH_FAILURE);
        }
        return authResponse.toString();
    }

    public String decodeAuthResult(String authResult) throws JSONException {
        JSONObject result = new JSONObject(new JSONTokener(authResult));
        return decodeAuthResultContent(result);
    }

    public String decodeAuthResultContent(JSONObject authResult) {
        return authResult.optString(Constants.VERIFICATION_RESULT);
    }

}
