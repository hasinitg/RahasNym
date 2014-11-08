package org.rahasnym.api.communication.policy;


import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 1:25 PM
 */
public enum DisclosureValues implements PolicyValues {
    PLAIN_TEXT(1), ZKP_I(3), ZKP_NI(2), ZKP_NI_S(4);

    private int priority;

    private DisclosureValues(int priorityValue) {
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
