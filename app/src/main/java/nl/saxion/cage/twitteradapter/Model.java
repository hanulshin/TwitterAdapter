package nl.saxion.cage.twitteradapter;

import com.github.scribejava.core.model.OAuth1AccessToken;

public class Model {

    //key & secret for getting accessToken
    private String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";

    private static Model model = new Model();

    private OAuth1AccessToken accessToken = null;

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
        return API_KEY;
    }

    public String getApiSecret() {
        return API_SECRET;
    }
}
