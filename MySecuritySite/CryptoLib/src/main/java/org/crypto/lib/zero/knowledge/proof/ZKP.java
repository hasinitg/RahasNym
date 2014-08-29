package org.crypto.lib.zero.knowledge.proof;

import org.crypto.lib.exceptions.CryptoAlgorithmException;

import java.security.NoSuchAlgorithmException;
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

    public E createChallengeForInteractiveZKP();

    public List<E> createChallengeForNonInteractiveZKP(X originalProblem, List<Y> helperProblems) throws NoSuchAlgorithmException;

    public List<E> createChallengeForNonInteractiveZKPWithSignature(X originalProblem, List<Y> helperProblems, byte[] message);

    public List<P> createProofForNonInteractiveZKP(X originalProblem, List<Y> helperProblems, List<E> challenges);

    public P createProofForInteractiveZKP(X originalProblem, Y helperProblem, E challenge);

    public boolean verifyInteractiveZKP(X originalProblem, Y helperProblem, E challenge, P proof);

    public boolean verifyNonInteractiveZKP(X originalProblem, List<Y> helperProblem, List<E> challenges, List<P> proofs);

    public boolean verifyNonInteractiveZKPWithSignature(X originalProblem, List<Y> helperProblem, byte[] message, List<E> challenges, List<P> proofs);

}
