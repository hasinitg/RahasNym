package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/26/15
 * Time: 2:29 PM
 */

import org.rahasnym.api.RahasNymException;

/**
 * Implementations of IDMM should register an implementation of PasswordCallBack
 * in in order for IDMM to obtain the user name and the password to be used in
 * creating the identity tokens.
 */
public class PasswordCallBackManager {
    private static PasswordCallBack passwordCallBack;

    public static void registerPasswordCallBack(PasswordCallBack callBack) {
        passwordCallBack = callBack;
    }

    public static String getNameOfCurrentUser() throws RahasNymException {
        if (passwordCallBack != null) {
            return passwordCallBack.getNameOfCurrentUser();
        }
        throw new RahasNymException("No password callback registered.");
    }

    public static String getPasswordOfCurrentUser() throws RahasNymException {
        if (passwordCallBack != null) {
            return passwordCallBack.getPasswordOfCurrentUser();
        }
        throw new RahasNymException("No password callback registered.");
    }
}
