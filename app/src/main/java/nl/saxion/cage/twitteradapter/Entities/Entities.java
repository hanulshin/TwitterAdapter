package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 *
 */
public class Entities {
    /**
     *
     */
    private List<Hashtags> hashtags;

    /**
     *
     */
    private List<Media> media;

    /**
     *
     */
    private List<URL> urls;

    /**
     *
     */
    public List<User_Mention> user_mentions;

    public List<Hashtags> getHashtags() {
        return hashtags;
    }

    public List<Media> getMedia() {
        return media;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public List<User_Mention> getUser_mentions() {
        return user_mentions;
    }

    /**
     * Default constructor
     */
    public Entities(List<Hashtags> hashtags, List<Media> media, List<URL> urls, List<User_Mention> user_mentions) {
        this.hashtags = hashtags;
        this.media = media;
        this.urls = urls;
        this.user_mentions = user_mentions;
    }
    public Entities(List<Hashtags> hashtags, List<URL> urls) {
        this.hashtags = hashtags;
        this.urls = urls;

    }


    public Entities(List<Hashtags> hashtags) {
        this.hashtags = hashtags;
    }





}