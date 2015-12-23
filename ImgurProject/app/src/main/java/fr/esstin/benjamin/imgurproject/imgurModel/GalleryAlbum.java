package fr.esstin.benjamin.imgurproject.imgurModel;

/**
 * Created by Benjamin on 02/12/2015.
 */
public class GalleryAlbum extends GalleryParents {
    public String id;
    public String title;
    public String description;
    public int datetime;
    public String cover;
    public int cover_width;
    public int cover_height;
    public String privacy;
    public String layout;
    public int views;
    public String link;
    public int ups;
    public int downs;
    public int points;
    public int score;
    public boolean is_album;
    public String vote;
    public boolean favorite;
    public boolean nsfw;
    public String topic;
    public int topic_id;
    public int images_count;
    public GalleryImage[] images;
}
