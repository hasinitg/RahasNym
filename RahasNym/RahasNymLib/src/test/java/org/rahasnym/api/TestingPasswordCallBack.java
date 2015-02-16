package org.rahasnym.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.idmapi.PasswordCallBack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 2/8/15
 * Time: 10:50 AM
 */
public class TestingPasswordCallBack implements PasswordCallBack{
    private static String fileName = null;

    public void setConfigurationFile(String file) {
        fileName = file;
    }

    public JSONObject readUserInfo() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String policy = "", line;
        while ((line = reader.readLine()) != null) {
            policy += line;
        }
        return new JSONObject(new JSONTokener(policy));
    }

    @Override
    public String getNameOfCurrentUser() throws RahasNymException {
        try {
            if (readUserInfo() != null) {
                return readUserInfo().optString("userName");
            } else {
                throw new RahasNymException("UserInfo not provided..");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RahasNymException("UserInfo not provided..");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("UserInfo not provided..");
        }
    }

    @Override
    public String getPasswordOfCurrentUser() throws RahasNymException {
        try {
            if (readUserInfo() != null) {
                return readUserInfo().optString("password");
            } else {
                throw new RahasNymException("UserInfo not provided..");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RahasNymException("UserInfo not provided..");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RahasNymException("UserInfo not provided..");
        }
    }
}
