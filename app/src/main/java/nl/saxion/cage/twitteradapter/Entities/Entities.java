package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 * 
 */
public class Entities {

    /**
     * Default constructor
     */
    public Entities(List<Hashtags> hashtags, List<Media> media, List<URL> urls, List<User_Mention> user_mentions) {
        this.hashtags = hashtags;
        this.media = media;
        this.urls = urls;
        this.user_mentions = user_mentions;
    }

    /**
     * 
     */
    public List<Hashtags> hashtags;

    /**
     * 
     */
    public List<Media> media;

    /**
     * 
     */
    public List<URL> urls;

    /**
     * 
     */
    public List<User_Mention> user_mentions;

}