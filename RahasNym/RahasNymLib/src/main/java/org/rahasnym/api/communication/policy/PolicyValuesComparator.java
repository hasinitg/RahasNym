package org.rahasnym.api.communication.policy;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 5:11 PM
 */
public class PolicyValuesComparator implements Comparator<PolicyValues> {
    @Override
    public int compare(PolicyValues pv1, PolicyValues pv2) {
        return pv2.getPriority() - pv1.getPriority();
    }
}
