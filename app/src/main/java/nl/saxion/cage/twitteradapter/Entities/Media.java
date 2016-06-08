package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 * 
 */
public class Media {


    /**
     * 
     */
    private String display_url;

    /**
     * 
     */
    private String expanded_url;

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
    private int[] indices;

    /**
     * 
     */
    private String media_url;

    /**
     * 
     */
    private String media_url_https;

    /**
     * 
     */
    private Sizes sizes;

    /**
     * 
     */
    private int source_status_id;

    /**
     * 
     */
    private String source_status_id_str;

    /**
     * 
     */
    private String type;

    /**
     *
     */
    private String url;

    /**
     * Default constructor
     */
    public Media(int[] indices, String url) {
        this.indices = indices;
        this.url = url;
    }



}