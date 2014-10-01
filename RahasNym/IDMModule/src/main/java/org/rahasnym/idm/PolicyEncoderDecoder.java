package org.rahasnym.idm;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 10/1/14
 * Time: 5:35 PM
 */

/**
 * This class handles reading Identity Verification Policy from file, writting to policy file
 * and converting policy object into JSON and vice versa.
 */
public class PolicyEncoderDecoder {

    public void writePolicyToFile(IDVPolicy policy){

    }

    public IDVPolicy readPolicyFromFile(String policyFilePath){
        return new IDVPolicy();
    }

    public String toJSON(IDVPolicy policy){
        return "";
    }

    public IDVPolicy fromPolicy(String JSONPolicy){
        return new IDVPolicy();
    }

}
