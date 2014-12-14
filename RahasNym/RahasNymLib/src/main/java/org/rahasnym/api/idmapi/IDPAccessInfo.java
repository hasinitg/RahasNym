package org.rahasnym.api.idmapi;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/13/14
 * Time: 4:40 PM
 */

/**
 * This class represents the access info of IDPs in IDMMConfig class, which is loaded from the user provided
 * config info about different IDPs corresponding to different attributes of the user.
 */
public class IDPAccessInfo {
    private String url;
    private String username;
    private String password;
    private String oauthToken;

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
