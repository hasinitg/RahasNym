package org.rahasnym.api.idenity;

import org.rahasnym.api.RahasNymException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/26/15
 * Time: 1:05 PM
 */

/**
 * Implementations of IDP and IDMM should implement this interface and register in
 * AttributeCallBackManager in order for IDMM and IDP to retrieve the attribute values,
 */
public interface AttributeCallBack {
    public String getAttributeValue(String userName, String attributeName) throws RahasNymException;
}
