package nl.saxion.cage.twitteradapter.Entities;

/**
 * class containing media info
 */
public class Media {

    /**
     * bounds of link for highlighting
     */
    private int[] indices;

    /**
     * Url used to retrieve media (pictures)
     */
    private String media_url;

    /**
     * Set variables
     */
    public Media(int[] indices, String url) {
        this.indices = indices;
        this.media_url = url;
    }

    public String getMedia_url() {
        return media_url;
    }
}