package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 * 
 */
public class Hashtags {

    /**
     * Default constructor
     *
     * @param indices
     * @param text
     */
    public Hashtags(List<Integer> indices, String text) {
        this.indices = indices;
        this.text = text;
    }

    /**
     * 
     */
    public List<Integer> indices;

    /**
     * 
     */
    public String text;


}