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
    public Hashtags(int indices[], String text) {
        this.indices = indices;
        this.text = text;

    }

    public int[] getIndices() {
        return indices;
    }

    public String getText() {
        return text;
    }

    /**
     * 
     */
    public int indices[];

    /**
     * 
     */
    public String text;


}