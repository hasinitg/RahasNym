package org.rahasnym.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.idenity.AttributeCallBack;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 2/8/15
 * Time: 10:51 AM
 */
public class TestingAttributeCallBack implements AttributeCallBack{
    private static String attributeStoreFile;
    private static JSONObject attributeStore;

    public void setAttributeStoreFile(String fileName) {
        attributeStoreFile = fileName;
    }

    @Override
    public String getAttributeValue(String userName, String attributeName) throws RahasNymException {
        try {
            if (attributeStore == null) {
                BufferedReader reader = new BufferedReader(new FileReader(attributeStoreFile));
                String policy = "", line;
                while ((line = reader.readLine()) != null) {
                    policy += line;
                }
                attributeStore = new JSONObject(new JSONTokener(policy));
            }
            JSONArray arr = attributeStore.optJSONArray("Users");
            int l = arr.length();
            for (int i = 0; i < l; i++) {
                JSONObject user = arr.optJSONObject(i);
                if (userName.equals(user.optString("name"))) {
                    return user.optString(attributeName);
                }
            }
        } catch (Exception e) {
            throw new RahasNymException("Error in connecting to attribute store.");
        }
        throw new RahasNymException("Error in reading the user attribute.");
    }
}
