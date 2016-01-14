package fr.esstin.benjamin.imgurproject.imgurModel;

import android.media.Image;

import com.google.gson.JsonObject;
import com.squareup.okhttp.RequestBody;

import java.io.File;

import fr.esstin.benjamin.imgurproject.imgurEnum.Section;
import fr.esstin.benjamin.imgurproject.imgurEnum.Sort;
import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Benjamin on 02/12/2015.
 */
public interface ImgurAPI {
    String server = "https://api.imgur.com";

    @Multipart
    @POST("/3/image")
    Call<ImageResponse> postImage(
            @Header("Authorization") String auth,
            @Part("image") RequestBody file,
            @Part("title") String title,
            @Part("description") String description
    );

    @GET("/3/gallery")
      Call<GalleryResponse> getGallery(
            @Header("Authorization") String auth
    );

    @GET("/3/gallery/{section}/{sort}/{page}/")
    Call<GalleryAlbumResponse> getGallery(
            @Header("Authorization") String auth,
            @Path("section") Section section,
            @Path("sort") Sort sort,
            @Path("page") int page,
            @Query("showViral") boolean showViral
    ); 

    @GET("/3/gallery/album/{id}")
    Call<GalleryAlbumResponse> getGalleryAlbum(
            @Header("Authorization") String auth,
            @Path("id") String id
    );

}
