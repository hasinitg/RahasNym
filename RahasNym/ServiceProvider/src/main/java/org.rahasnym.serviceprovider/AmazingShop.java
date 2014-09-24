package org.rahasnym.serviceprovider;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/13/13
 * Time: 6:57 PM
 */
@Path("/")
public class AmazingShop {
    @POST
    public void authorizeTransaction(@HeaderParam(SPConstants.PURDUE_ID_COMMITMENT) String purdueIDCommitment,
                                     @HeaderParam(SPConstants.HEALTH_INSURANCE_ID_COMMITMENT) String insuranceIDCommitment,
                                     String counterNumber){
        System.out.println("AmazingShop Service Invoked...");
    }
    @GET
    public void printSomething(){
        System.out.println("Get was called...");
    }
}
