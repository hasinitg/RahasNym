package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/26/15
 * Time: 2:29 PM
 */

import org.rahasnym.api.RahasNymException;

/**
 * An implementation of this interface should be registered at the PasswordCallBackManager,
 * by the IDMM implementations in order for IDMM API to obtain the username and the password
 * of the user who is using the device.
 */
public interface PasswordCallBack {

    public String getNameOfCurrentUser() throws RahasNymException;

    public String getPasswordOfCurrentUser() throws RahasNymException;
}
