package org.rahasnym.serviceprovider;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/17/14
 * Time: 1:55 PM
 */

import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;

import javax.ws.rs.core.Response;

/**
 * This intercepts the requests coming to the JAX-RS services hosted in this app and performs authentication
 * based on the security policies of the services.
 */
public class AuthenticationFilter implements RequestHandler{
    @Override
    public Response handleRequest(Message message, ClassResourceInfo classResourceInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
