package fr.esstin.benjamin.imgurproject.imgurModel;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "GalleryAlbum{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", datetime=" + datetime +
                ", cover='" + cover + '\'' +
                ", cover_width=" + cover_width +
                ", cover_height=" + cover_height +
                ", privacy='" + privacy + '\'' +
                ", layout='" + layout + '\'' +
                ", views=" + views +
                ", link='" + link + '\'' +
                ", ups=" + ups +
                ", downs=" + downs +
                ", points=" + points +
                ", score=" + score +
                ", is_album=" + is_album +
                ", vote='" + vote + '\'' +
                ", favorite=" + favorite +
                ", nsfw=" + nsfw +
                ", topic='" + topic + '\'' +
                ", topic_id=" + topic_id +
                ", images_count=" + images_count +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
