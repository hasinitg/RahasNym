package org.rahasnym.api.communication.policy;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 2:19 PM
 */
public enum SubjectVerificationValues implements PolicyValues {
    BIOMETRIC_BOUND(10), SP_BOUND(5), HIDDEN_PSEUDONYM_BOUND(2), PSEUDONYM_BOUND(1), BIOMETRIC_BOUND_N_PSEUDONYM_BOUND(11),
    BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND(12), PSEUDONYM_BOUND_N_SP_BOUND(6), HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND(7),
    BIOMETRIC_BOUND_N_PSEUDONYM_BOUND_N_SP_BOUND(16), BIOMETRIC_BOUND_N_HIDDEN_PSEUDONYM_BOUND_N_SP_BOUND(17);

    private int priority;

    private SubjectVerificationValues(int priorityValue) {
        priority = priorityValue;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compare(PolicyValues pv1, PolicyValues pv2) {
        return pv2.getPriority() - pv1.getPriority();
    }
}
