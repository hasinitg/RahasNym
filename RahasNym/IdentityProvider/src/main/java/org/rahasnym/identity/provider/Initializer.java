package org.rahasnym.identity.provider;

import org.crypto.lib.exceptions.CryptoAlgorithmException;
import org.rahasnym.api.idenity.AttributeCallBack;
import org.rahasnym.api.idenity.AttributeCallBackManager;
import org.rahasnym.api.idpapi.IDPConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/7/15
 * Time: 10:51 PM
 */

/**
 * This class performs the initialization stuff at the startup of the IDP webapp.
 */
public class Initializer implements ServletContextListener {
    private String IDPKeyStorePath = "IDPkeystore.jks";
    private String keyStorePass = "rahasnymIDP";
    private String certAlias = "rahasnymIDPCert";
    private String attributeStoreFile = "attributeStore";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        IDPConfig idpConfig = null;
        try {
            idpConfig = IDPConfig.getInstance();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] storePass = keyStorePass.toCharArray();
            InputStream keyStoreFile = getClass().getClassLoader().getResourceAsStream(IDPKeyStorePath);
            keyStore.load(keyStoreFile, storePass);
            idpConfig.setRSAPrivateKey((PrivateKey) keyStore.getKey(certAlias, storePass));
            idpConfig.setCertificateAlias(certAlias);

            //register attribute call back handler.
            AttributeCallBack attributeCallBack = new BasicAttributeHandler();
            ((BasicAttributeHandler) attributeCallBack).setAttributeStoreFile(attributeStoreFile);
            AttributeCallBackManager.registerAttributeCallBack(attributeCallBack);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CryptoAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
