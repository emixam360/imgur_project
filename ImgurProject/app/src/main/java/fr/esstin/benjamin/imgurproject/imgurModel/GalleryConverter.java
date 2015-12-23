package fr.esstin.benjamin.imgurproject.imgurModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by Benjamin on 02/12/2015.
 */
public class GalleryConverter {

    public GalleryConverter(){
    }

    public ArrayList<GalleryParents> convertGallery(ArrayList<JsonObject> objs){
        ArrayList<GalleryParents> Images = new ArrayList<>();
        for (JsonObject g : objs) {
            if(g.get("is_album").getAsBoolean()){
                Images.add(new Gson().fromJson(g.toString(), GalleryAlbum.class));
            }else{
                Images.add(new Gson().fromJson(g.toString(), GalleryImage.class));
            }
        }
        return Images;
    }

    public GalleryParents convertAlbum(JsonObject objs){
        GalleryParents album = new Gson().fromJson(objs.toString(), GalleryAlbum.class);
        return album;
    }
}
