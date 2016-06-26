package nl.saxion.cage.twitteradapter;

import android.graphics.Bitmap;

import nl.saxion.cage.twitteradapter.Entities.Entities;

/**
 * class containing information of user object
 */
public class Users {

    /**
     * description the user has provided of themself
     */
    public String description;

    /**
     * number of followers the user has
     */
    public int followers_count;

    /**
     * number of friends the user has
     */
    public int friends_count;

    /**
     * name of the user
     */
    private String name;

    /**
     * url of the profile image to be fetched
     */
    private String profile_image_url;

    /**
     * screen name of the user
     */
    public String screen_name;

    /**
     * number of statuses the user has made
     */
    public int statuses_count;

    /**
     * basic user info
     *
     * @param screen_name screen name of the user
     * @param name name of the user
     * @param profile_image_url url for loading profile image
     */
    public Users(String screen_name, String name, String profile_image_url) {
        this.screen_name = screen_name;
        this.name = name;
        this.profile_image_url = profile_image_url;
    }

    /**
     * advenced user info (for logged in user)
     *
     * @param screen_name screen name of the user
     * @param name name of the user
     * @param profile_image_url url for loading profile image
     * @param description description of user
     * @param followers_count number of followers
     * @param friends_count number of friends
     * @param statuses_count number of statuses
     */
    public Users(String screen_name, String name, String profile_image_url, String description, int followers_count, int friends_count, int statuses_count ) {
        this.screen_name = screen_name;
        this.name = name;
        this.profile_image_url = profile_image_url;
        this.description = description;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.statuses_count = statuses_count;
    }

    public String getDescription() {
        return description;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getName() {
        return name;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

}