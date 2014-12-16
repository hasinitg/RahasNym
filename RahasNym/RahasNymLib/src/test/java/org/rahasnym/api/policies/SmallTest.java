package org.rahasnym.api.policies;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

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
        JSONObject root = new JSONObject();
        root.put("IDT", request);
        String r = root.toString();
        JSONObject cover = new JSONObject(r);
        cover.put("x", "y");
        System.out.println(cover.toString());

        String UUID = java.util.UUID.randomUUID().toString();
        System.out.println(UUID);
    }
}
