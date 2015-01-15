package org.rahasnym.serviceprovider;

import org.rahasnym.api.idenity.IdentityProof;
import org.rahasnym.api.idenity.IdentityToken;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/14/15
 * Time: 1:22 PM
 */
public class User {
    private String userName;
    private String hashPassword;
    private byte[] salt;
    private IdentityToken emailIDT;
    private IdentityProof emailProof;
    private IdentityToken addressIDT;
    private IdentityProof addressProof;
    private IdentityToken CCNIDT;
    private boolean isFreeShippingEnabled;
    private IdentityProof CCNProof;

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public boolean isFreeShippingEnabled() {
        return isFreeShippingEnabled;
    }

    public void setFreeShippingEnabled(boolean freeShippingEnabled) {
        isFreeShippingEnabled = freeShippingEnabled;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public IdentityToken getEmailIDT() {
        return emailIDT;
    }

    public void setEmailIDT(IdentityToken emailIDT) {
        this.emailIDT = emailIDT;
    }

    public IdentityProof getEmailProof() {
        return emailProof;
    }

    public void setEmailProof(IdentityProof emailProof) {
        this.emailProof = emailProof;
    }

    public IdentityToken getAddressIDT() {
        return addressIDT;
    }

    public void setAddressIDT(IdentityToken addressIDT) {
        this.addressIDT = addressIDT;
    }

    public IdentityProof getAddressProof() {
        return addressProof;
    }

    public void setAddressProof(IdentityProof addressProof) {
        this.addressProof = addressProof;
    }

    public IdentityToken getCCNIDT() {
        return CCNIDT;
    }

    public void setCCNIDT(IdentityToken CCNIDT) {
        this.CCNIDT = CCNIDT;
    }

    public IdentityProof getCCNProof() {
        return CCNProof;
    }

    public void setCCNProof(IdentityProof CCNProof) {
        this.CCNProof = CCNProof;
    }





}
