package org.rahasnym.idm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 9/29/14
 * Time: 8:06 AM
 */

/**
 * Upon receiving the policy from server, this combines it with client's policy and return the
 * combined policy for IDMModule to obtain a matching token. Also, if there is a conflict in policy combining,
 * this notifies the user through the notification interface.
 */
public class PolicyHandler {
    public PolicyDecision getPolicyDecision(String policyString){
        //create a policy object from server policy

        //obtain the client policy object

        //combine two policies according to the policy combining algorithm

        //if there is a conflict, notify the user

        //return the policy decision
        return new PolicyDecision();
    }

    private PolicyDecision evaluate(){
        /*assumptions: */

        return new PolicyDecision();
    }
}
