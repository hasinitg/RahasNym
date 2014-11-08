package org.rahasnym.api.communication.policy;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 11/7/14
 * Time: 4:51 PM
 */

/**
 * This interface enforces two methods on any enum type that might be declared
 * to encapsulate different policy values.
 * Since enum doesn't support extends, we can not have an abstract class
 * implementing those two methods - which have the same logic and let each enum to extend it.
 */
public interface PolicyValues extends Comparator<PolicyValues> {
    public int getPriority();
}
