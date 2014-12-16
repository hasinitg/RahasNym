package org.rahasnym.api.idmapi;

import org.rahasnym.api.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/15/14
 * Time: 8:02 PM
 */
public class ProofStore<String, ProofInfo> extends LinkedHashMap {

    private int maxSize = Constants.MAX_SIZE_PROOF_MAP_IN_MEMORY;

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }
}
