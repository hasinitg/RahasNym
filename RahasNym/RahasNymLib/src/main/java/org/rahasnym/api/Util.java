package org.rahasnym.api;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 1:47 PM
 */

import org.crypto.lib.commitments.pedersen.PedersenPublicParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.math.BigInteger;

/**
 * This contains the utility functions of RahasNymLib
 */
public class Util {

    public PedersenPublicParams extractPedersenParamsFromIDT(JSONObject jsonIDT) throws JSONException {
        //JSONObject IDT = new JSONObject(new JSONTokener(IDTString));
        JSONObject pedersenParams = jsonIDT.optJSONObject(Constants.PEDERSEN_PARAMS);
        PedersenPublicParams params = new PedersenPublicParams();
        params.setP(new BigInteger(pedersenParams.optString(Constants.P_PARAM)));
        params.setQ(new BigInteger(pedersenParams.optString(Constants.Q_PARAM)));
        params.setG(new BigInteger(pedersenParams.optString(Constants.G_PARAM)));
        params.setH(new BigInteger(pedersenParams.optString(Constants.H_PARAM)));
        return params;
    }
}
