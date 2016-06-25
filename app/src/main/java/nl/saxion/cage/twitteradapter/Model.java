package nl.saxion.cage.twitteradapter;

import com.github.scribejava.core.model.OAuth1AccessToken;

public class Model {
    private static Model model = new Model();

    private OAuth1AccessToken accessToken = null;

    public static Model getInstance(){
        return model;
    }

    protected OAuth1AccessToken getAccessToken(){
        return accessToken;
    }

    protected void setAccessToken(OAuth1AccessToken accessToken){
        this.accessToken = accessToken;
    }
}
