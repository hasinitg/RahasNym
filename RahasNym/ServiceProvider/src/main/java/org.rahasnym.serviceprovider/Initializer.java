package org.rahasnym.serviceprovider;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/8/15
 * Time: 12:12 AM
 */

import org.rahasnym.api.RahasNymException;
import org.rahasnym.api.verifierapi.VerifierCallBackManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class does the initialization stuff at the startup of the SP webapp.
 */
public class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //register trust store callback impl in verifier API
        try {
            TrustStoreCallBackImpl trustStoreCallBackImpl = new TrustStoreCallBackImpl();
            VerifierCallBackManager.registerTrustStoreCallBack(trustStoreCallBackImpl);
        } catch (RahasNymException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
