package org.rahasnym.api.communication.policy;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 3:58 PM
 */
public enum PseudonymCardinalityValues implements PolicyValues {
    MULTIPLE(1), SINGLE(2);

    private int priority;

    private PseudonymCardinalityValues(int priorityValues) {
        priority = priorityValues;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int compare(PolicyValues pv1, PolicyValues pv2) {
        return pv2.getPriority() - pv1.getPriority();
    }
}
