package nl.saxion.cage.twitteradapter;

import com.github.scribejava.core.model.OAuth1AccessToken;

/**
 * Model for storing api key/secret, and accessToken
 */
public class Model {

    /**
     * stores api key for signing requests
     */
    private String apiKey = "BABNgm313dL2rRXf3iRM11lL8";

    /**
     * stores api secret for signing requests
     */
    private String apiSecret = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";

    /**
     * creates the initial model that will be accessible from anywhere
     */
    private static Model model = new Model();

    /**
     * accessToken that will be set after loginActivity gets result
     */
    private OAuth1AccessToken accessToken = null;

    /**
     * gets the static instance of the model object
     * @return instance of Model
     */
    public static Model getInstance(){
        return model;
    }

    public OAuth1AccessToken getAccessToken(){
        return accessToken;
    }

    public void setAccessToken(OAuth1AccessToken accessToken){
        this.accessToken = accessToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
