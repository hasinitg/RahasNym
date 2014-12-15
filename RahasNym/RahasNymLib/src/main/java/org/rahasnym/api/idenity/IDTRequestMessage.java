package org.rahasnym.api.idenity;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 12:41 PM
 */

/**
 * This encapsulates the information sent by the IDMM to the IDP when requesting the IDT.
 */
public class IDTRequestMessage {
    private String attributeName;
    private String encryptedSecret;
    private String pseudonym;
    private String spIdentity;
    private boolean isSinglePseudonym;
    private boolean biometricIdentityRequired;

    public String getEncryptedSecret() {
        return encryptedSecret;
    }

    public void setEncryptedSecret(String encryptedSecret) {
        this.encryptedSecret = encryptedSecret;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getSpIdentity() {
        return spIdentity;
    }

    public void setSpIdentity(String spIdentity) {
        this.spIdentity = spIdentity;
    }

    public boolean isSinglePseudonym() {
        return isSinglePseudonym;
    }

    public void setSinglePseudonym(boolean singlePseudonym) {
        isSinglePseudonym = singlePseudonym;
    }

    public boolean isBiometricIdentityRequired() {
        return biometricIdentityRequired;
    }

    public void setBiometricIdentityRequired(boolean biometricIdentityRequired) {
        this.biometricIdentityRequired = biometricIdentityRequired;
    }
}
