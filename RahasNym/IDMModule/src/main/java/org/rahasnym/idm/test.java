package org.rahasnym.idm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 2/8/15
 * Time: 12:20 AM
 */
public class test {
    public static void main(String[] args) throws IOException, JSONException {
        String file = "/home/hasini/Hasini/Experimenting/RahasNym/IDMModule/src/main/resources/attributeStore";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String policy = "", line;
        while ((line = reader.readLine()) != null) {
            policy += line;
        }
        JSONObject idstore = new JSONObject(new JSONTokener(policy));
        JSONArray arr = idstore.optJSONArray("Users");
        System.out.println(arr);
        int l = arr.length();
        String currentUser = "hasini";
        for (int i = 0; i < l; i++) {
            JSONObject user = arr.optJSONObject(i);
            if (currentUser.equals(user.optString("name"))) {
                System.out.println(user);
            }
        }

    }
}
