package org.rahasnym.serviceprovider;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/14/15
 * Time: 1:23 PM
 */

import org.crypto.lib.Hash.SHA;
import org.crypto.lib.util.Base64;
import org.crypto.lib.util.CryptoUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the in-memory user store.
 * In the prototype, this also keeps track of the logged in users and users who have signed up with the session-ids
 * issued by the SP in order to help function the jsps.
 */
public class UserStore {
    //session ids of the users who have logged in.
    private static Map<String, String> loggedInUsers = new HashMap<>();

    //temporary list of session ids of just signed up users.
    private static List<String> justSignedUp = new ArrayList<>();

    //map of all enrolled users
    private static Map<String, User> signedUpUsers = new HashMap<>();

    private static volatile UserStore userStore = null;

    public static UserStore getInstance() {
        if (userStore == null) {
            synchronized (UserStore.class) {
                if (userStore == null) {
                    userStore = new UserStore();
                }
            }
        }
        return userStore;
    }

    public void saveEnrolledUser(User user) throws NoSuchAlgorithmException {
        byte[] salt = CryptoUtil.generateSalt(8);
        String hashEncodedPassword = getHashEncodedPassword(user.getHashPassword(), salt);
        user.setSalt(salt);
        user.setHashPassword(hashEncodedPassword);
        signedUpUsers.put(user.getUserName(), user);
    }

    public User getUser(String userName) {
        return signedUpUsers.get(userName);
    }

    public boolean isEnrolled(String userName) {
        return signedUpUsers.containsKey(userName);
    }

    public boolean authenticate(String userName, String password) {
        try {
            User user = signedUpUsers.get(userName);
            String pass = user.getHashPassword();
            byte[] saltVal = user.getSalt();
            String currentPass = getHashEncodedPassword(password, saltVal);
            if (currentPass.equals(pass)) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            //todo: properly log the error
            e.printStackTrace();
            return false;
        }
    }

    public boolean isJustSignedUp(String sessionID) {
        return justSignedUp.contains(sessionID);
    }

    public void addJustSignedUp(String sessionID) {
        justSignedUp.add(sessionID);
    }

    public void addLoggedIn(String loggedInSessionID, String userName) {
        loggedInUsers.put(loggedInSessionID, userName);
    }

    public boolean isLoggedIn(String loggedInSessionID) {
        return loggedInUsers.containsKey(loggedInSessionID);
    }

    public String getLoggedInUserName(String sessionID) {
        return loggedInUsers.get(sessionID);
    }

    public void removeLoggedIn(String loggedInSessionID) {
        loggedInUsers.remove(loggedInSessionID);
    }

    private String getHashEncodedPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        byte[] saltedPassword = new byte[password.getBytes().length + salt.length];
        System.arraycopy(salt, 0, saltedPassword, 0, salt.length);
        System.arraycopy(password.getBytes(), 0, saltedPassword, salt.length, password.getBytes().length);
        return Base64.encodeBytes(SHA.SHA256(saltedPassword));
    }

}
