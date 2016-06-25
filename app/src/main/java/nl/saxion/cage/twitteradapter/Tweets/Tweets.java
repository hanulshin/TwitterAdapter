package nl.saxion.cage.twitteradapter.Tweets;

import java.util.*;

import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Users;

/**
 * 
 */
public class Tweets  {

    /**
     * Default constructor
     */
    public Tweets(Users user, String text, int retweet_count, String created_at, int favourite_count, Entities entities) {
        this.user = user;
        this.text = text;
        this.retweet_count = retweet_count;
        this.created_at = created_at;
        this.favourite_count = favourite_count;
        this.entities = entities;
    }


    public String getId_str() {
        return id_str;
    }

    public Tweets(Users user, String text, int retweet_count, String created_at, int favourite_count, Entities entities, String id_str) {
        this.user = user;
        this.text = text;
        this.retweet_count = retweet_count;
        this.created_at = created_at;
        this.favourite_count = favourite_count;
        this.entities = entities;
        this.id_str = id_str;

    }

    public int getId() {
        return id;
    }
    public int getFavourite_count() {
        return favourite_count;
    }

    public Users getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Entities getEntities() {
        return entities;
    }

    /**
     * 
     */
    private Object annotations;

    /**
     * 
     */
    private String created_at;

    /**
     * 
     */
    private Current_User_Retweet current_user_retweet;

    /**
     * 
     */
    private Entities entities;

    /**
     * 
     */
    private int favourite_count;

    /**
     * 
     */
    private Boolean favourited;

    /**
     * 
     */
    private String filter_level;

    /**
     * 
     */
    private Object geo;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String id_str;

    /**
     * 
     */
    private String in_reply_to_screen_name;

    /**
     * 
     */
    private int in_reply_to_status_id;

    /**
     * 
     */
    private String in_reply_to_status_id_str;

    /**
     * 
     */
    private int in_reply_to_user_id;

    /**
     * 
     */
    private String in_reply_to_user_id_str;

    /**
     * 
     */
    private String lang;

    /**
     * 
     */
    private Places place;

    /**
     * 
     */
    private Boolean possibly_sensitive;

    /**
     * 
     */
    private int quoted_status_id;

    /**
     * 
     */
    private String quoted_status_id_str;

    /**
     * 
     */
    private Tweets quoted_status;

    /**
     * 
     */
    private Object scopes;

    /**
     * 
     */
    private int retweet_count;

    /**
     * 
     */
    private Boolean retweeted;

    /**
     * 
     */
    private Tweets retweeted_status;

    /**
     * 
     */
    private String source;

    /**
     * 
     */
    private String text;

    /**
     * 
     */
    private Boolean truncated;

    /**
     * 
     */
    private Users user;

    /**
     * 
     */
    private Boolean withheld_copyright;

    /**
     * 
     */
    private List<String> withheld_in_countries;

    /**
     * 
     */
    private String withheld_scope;
}