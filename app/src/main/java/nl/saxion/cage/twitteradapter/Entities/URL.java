package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 * 
 */
public class URL {


    /**
     * 
     */
    public String display_url;

    /**
     * 
     */
    public String expanded_url;

    /**
     * 
     */
    //public List<Integer> indices;
    private int indices[];
    /**
     * 
     */


    public String url;

    /**
     * Default constructor
     */
    public URL(int indices[], String url) {
        this.indices = indices;
        this.url = url;

    }

    public String getUrl() {
        return url;
    }

    public int[] getIndices() {
        return indices;
    }
}