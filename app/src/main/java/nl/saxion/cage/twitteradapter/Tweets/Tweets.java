package nl.saxion.cage.twitteradapter.Tweets;

import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Users;

/**
 * 
 */
public class Tweets  {

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
    private int retweet_count;

    /**
     *
     */
    private String text;

    /**
     *
     */
    private Users user;

    private boolean favourited = false;

    public Tweets(Users user, String text, int retweet_count, String created_at, int favourite_count, Entities entities, String id_str) {
        this.user = user;

        this.text = text;
        this.retweet_count = retweet_count;
        this.created_at = created_at;
        this.favourite_count = favourite_count;
        this.entities = entities;
        this.id_str = id_str;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public String getId_str() {
        return id_str;
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


}