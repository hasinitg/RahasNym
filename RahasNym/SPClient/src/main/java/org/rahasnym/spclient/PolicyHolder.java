package org.rahasnym.spclient;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/8/15
 * Time: 12:43 PM
 */

/**
 * This is to keep the SP policy in a globally accessible location so that it doesn't need to be retrieved always.
 */
public class PolicyHolder {
    private String policy = null;
    private static volatile PolicyHolder policyHolder = null;

    public static PolicyHolder getInstance() {
        if (policyHolder == null) {
            synchronized (PolicyHolder.class) {
                if (policyHolder == null) {
                    policyHolder = new PolicyHolder();
                    return policyHolder;
                }
            }
        }
        return policyHolder;
    }

    public void setPolicy(String spPolicy){
        policy = spPolicy;
    }

    public String getPolicy(){
        return policy;
    }
}
