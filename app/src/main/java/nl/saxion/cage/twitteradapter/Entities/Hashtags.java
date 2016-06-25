package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 * Class containing data for a Hashtag within a tweet
 */
public class Hashtags {

    /**
     * bounds of text for highlighting
     */
    private int indices[];

    /**
     * text of hashtag
     */
    private String text;

    /**
     * Sets variables
     *
     * @param indices bounds of the text for highlighting
     * @param text the text
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

}