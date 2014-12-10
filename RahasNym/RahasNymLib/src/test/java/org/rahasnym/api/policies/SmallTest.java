package org.rahasnym.api.policies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/10/14
 * Time: 11:58 AM
 */
public class SmallTest {
    public static void main(String[] args) throws JSONException {
        JSONObject request = new JSONObject();
        request.append("operation", "login");
        request.append("policy", "spPolicy");
        System.out.println(request.toString());
    }
}
