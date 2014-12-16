package org.rahasnym.api.verifierapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/16/14
 * Time: 2:04 PM
 */

/**
 * This maintains the single proof store at the verifier.
 */
public class ProofStoreManager {
    private static volatile ProofStoreManager proofStoreManager;
    private static ProofStore<String, ProofInfo> proofStore;

    private ProofStoreManager() {
        proofStore = new ProofStore<>();
    }

    public static ProofStoreManager getInstance() {
        if (proofStoreManager == null) {
            synchronized (ProofStoreManager.class) {
                if (proofStoreManager == null) {
                    proofStoreManager = new ProofStoreManager();
                    return proofStoreManager;
                }
            }
        }
        return proofStoreManager;
    }

    public void addProofInfo(String sessionID, ProofInfo proofInfo){
        proofStore.put(sessionID, proofInfo);
    }

    public ProofInfo getProofInfo(String sessionID){
        return (ProofInfo) proofStore.get(sessionID);
    }
}
