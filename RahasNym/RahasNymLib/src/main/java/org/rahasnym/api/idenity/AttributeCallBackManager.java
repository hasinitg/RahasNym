package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/26/15
 * Time: 1:05 PM
 */

import org.rahasnym.api.RahasNymException;

/**
 * The implementations of AttributeCallBack are registered here and the
 * IDMM API and IDP API obtains the user attribute values through such call back
 * implementations
 */
public class AttributeCallBackManager {
    private static AttributeCallBack attributeCallBack;

    public static void registerAttributeCallBack(AttributeCallBack callBack) {
        attributeCallBack = callBack;
    }

    public static String getAttributeValue(String userName, String attributeName) throws RahasNymException {
        if (attributeCallBack != null) {
            return attributeCallBack.getAttributeValue(userName, attributeName);
        }
        throw new RahasNymException("No attribute callback registered.");
    }
}
