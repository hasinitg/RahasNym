package org.rahasnym.api.verifierapi;

import org.rahasnym.api.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/15/14
 * Time: 8:02 PM
 */

/**
 * Identity proofs against the session id are stored here. Maximum proof size in memory is configurable to avoid memory leaks.
 * Todo: This can be extended to have a call back handler to get the proof, given the session id so that the applications using the
 * RahasNym API can maintain proofs and session ids in the way they want.
 * @param <String>
 * @param <ProofInfo>
 */
public class ProofStore<String, ProofInfo> extends LinkedHashMap {

    private int maxSize = Constants.MAX_SIZE_PROOF_MAP_IN_MEMORY;

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }
}
