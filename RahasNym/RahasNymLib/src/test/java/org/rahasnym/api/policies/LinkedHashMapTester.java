package org.rahasnym.api.policies;

import org.rahasnym.api.idmapi.ProofInfo;
import org.rahasnym.api.idmapi.ProofStore;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/15/14
 * Time: 8:02 PM
 */
public class LinkedHashMapTester {
    public static void main(String[] args) {
        ProofStore<String, ProofInfo> store = new ProofStore<String, ProofInfo>();
        for (int i = 0; i < 200; i++) {
            store.put(String.valueOf(i), new ProofInfo());
        }
        for (Object sessionID : store.keySet()) {
            System.out.println((String) sessionID);
        }
    }
}
