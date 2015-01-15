package org.rahasnym.serviceprovider;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/14/15
 * Time: 1:23 PM
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the in-memory user store.
 */
public class UserStore {
    //temporary list of session ids of just signed up users.
    private static List<String> justSignedUp = new ArrayList<>();

    //map of signed up users
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

    public void saveEnrolledUser(User user) {
        signedUpUsers.put(user.getUserName(), user);
    }

    public boolean isEnrolled(String userName) {
        return signedUpUsers.containsKey(userName);
    }

    public boolean authenticate(String userName, String password) {
        //todo:
        return false;
    }

    public boolean isJustSignedUp(String sessionID) {
        return justSignedUp.contains(sessionID);
    }

    public void addJustSignedUp(String sessionID) {
        justSignedUp.add(sessionID);
    }

}
