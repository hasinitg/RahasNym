package org.crypto.lib.zero.knowledge.proof;

import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 8/22/14
 * Time: 4:34 AM
 */
public interface ZKP<X, Y, E, P> {
    public Y createHelperProblem(X originalProblem) throws CryptoAlgorithmException;

    public List<Y> createHelperProblems(X originalProblem);

    public E createInteractiveChallenge();

    public List<E> createNonInteractiveChallenge(byte[] hash);

    public List<P> createNonInteractiveProof(X OriginalProblem, List<Y> HelperProblems, List<E> challenges);

    public P createInteractiveProof(X originalProblem, Y helperProblem, E challenge);

    public boolean verifyInteractiveProof(X originalProblem, Y helperProblem, E challenge, P proof);

    public boolean verifyNonInteractiveProof(X originalProblem, List<Y> helperProblem, List<E> challenges, List<P> proofs);

}
