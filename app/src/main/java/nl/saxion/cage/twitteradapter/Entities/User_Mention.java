package nl.saxion.cage.twitteradapter.Entities;

import java.util.*;

/**
 *
 */
public class User_Mention {

    /**
     * Default constructor
     */
    public User_Mention() {
    }

    /**
     *
     */
    public int id;

    /**
     *
     */
    public String id_str;

    public User_Mention(int[] indices) {
        this.indices = indices;
    }

    /**
     *
     */
    private int indices[];

    public int[] getIndices() {
        return indices;
    }

    /**
     *
     */
    public String name;

    /**
     *
     */
    public String screen_name;

}