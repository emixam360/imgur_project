package fr.esstin.benjamin.imgurproject.imgurModel;

/**
 * Created by Benjamin on 02/12/2015.
 */
public class GalleryImage extends GalleryParents {
    public String id;
    public String title;
    public String description;
    public int datetime;
    public String type;
    public boolean animated;
    public int width;
    public int height;
    public int size;
    public int views;
    public long bandwidth;
    public String link;
    public String gifv;
    public String mp4;
    public String webm;
    public boolean looping;
    public int vote;
    public boolean favorite;
    public boolean nsfw;
    public String topic;
    public int topic_id;
    public String section;
    public int ups;
    public int downs;
    public int points;
    public int score;
    public boolean is_album;

    @Override
    public String toString() {
        return "GalleryImage{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", datetime=" + datetime +
                ", type='" + type + '\'' +
                ", animated=" + animated +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                ", views=" + views +
                ", bandwidth=" + bandwidth +
                ", link='" + link + '\'' +
                ", gifv='" + gifv + '\'' +
                ", mp4='" + mp4 + '\'' +
                ", webm='" + webm + '\'' +
                ", looping=" + looping +
                ", vote=" + vote +
                ", favorite=" + favorite +
                ", nsfw=" + nsfw +
                ", topic='" + topic + '\'' +
                ", topic_id=" + topic_id +
                ", section='" + section + '\'' +
                ", ups=" + ups +
                ", downs=" + downs +
                ", points=" + points +
                ", score=" + score +
                ", is_album=" + is_album +
                '}';
    }
}
