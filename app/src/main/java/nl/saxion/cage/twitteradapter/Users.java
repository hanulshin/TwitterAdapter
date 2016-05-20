package nl.saxion.cage.twitteradapter;

import android.graphics.Bitmap;

import nl.saxion.cage.twitteradapter.Entities.Entities;

/**
 *
 */
public class Users {

    /**
     * Default constructor
     */
    public Users(String screen_name, String name, String profile_image_url, Bitmap profile_image_bmp) {
        this.screen_name = screen_name;
        this.name = name;
        this.profile_image_url=profile_image_url;
        this.profile_image_bmp = profile_image_bmp;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getName() {
        return name;
    }
    /**
     *
     */
    //public Type following;

    /**
     *
     */
    public boolean contributors_enabled;

    /**
     *
     */
    public String created_at;

    /**
     *
     */
    public boolean default_profile;

    /**
     *
     */
    public boolean default_profile_image;

    /**
     *
     */
    public String description;

    /**
     *
     */
    private Entities entities;

    /**
     *
     */
    public int favourites_count;

    /**
     *
     */
    //public Type follow_request_sent;

    /**
     *
     */
    public int followers_count;

    /**
     *
     */
    public int friends_count;

    /**
     *
     */
    public boolean geo_enabled;

    /**
     *
     */
    public int id;

    /**
     *
     */
    public String id_str;

    /**
     *
     */
    public boolean is_translator;

    /**
     *
     */
    public String lang;

    /**
     *
     */
    public int listed_count;

    /**
     *
     */
    public String location;

    /**
     *
     */
    private String name;

    /**
     *
     */
    public boolean notifications;

    /**
     *
     */
    public String profile_background_color;

    /**
     *
     */
    public String profile_background_image_url;

    /**
     *
     */
    public String profile_background_image_url_https;

    /**
     *
     */
    public boolean profile_background_tile;



    /**
     *
     */
    public String profile_banner_url;

    /**
     *
     */
    private String profile_image_url;

    private Bitmap profile_image_bmp;

    /**
     *
     */
    public String profile_image_url_https;

    /**
     *
     */
    public String profile_link_color;

    /**
     *
     */
    public String profile_sidebar_border_color;

    /**
     *
     */
    public String profile_sidebar_fill_color;

    /**
     *
     */
    public String profile_text_color;

    /**
     *
     */
    public boolean profile_use_background_image;

    /**
     *
     */
    public boolean is_protected;

    /**
     *
     */
    public String screen_name;

    /**
     *
     */
    public boolean show_all_inline_media;

    /**
     *
     */
    public Entities status;

    /**
     *
     */
    public int statuses_count;

    /**
     *
     */
    public String time_zone;

    /**
     *
     */
    public String url;

    /**
     *
     */
    public int utc_offset;

    /**
     *
     */
    public boolean verified;

    /**
     *
     */
    public String withheld_in_countries;

    /**
     *
     */
    public String withheld_scope;

    public String getProfile_image_url() {
        return profile_image_url;
    }

}