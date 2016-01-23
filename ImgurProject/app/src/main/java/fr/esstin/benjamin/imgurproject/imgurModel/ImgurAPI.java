package fr.esstin.benjamin.imgurproject.imgurModel;

import com.squareup.okhttp.RequestBody;


import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by Benjamin on 02/12/2015.
 */
public interface ImgurAPI {
    String server = "https://api.imgur.com";

    @Multipart
    @POST("/3/image")
    Call<ImageResponse> postImage(
            @Header("Authorization") String auth,
            @Part("image") RequestBody file
    );

    @GET("/3/gallery/{section}/{sort}/{page}/")
    Call<GalleryResponse> getGallery(
            @Header("Authorization") String auth,
            @Path("section") String section,
            @Path("sort") String sort,
            @Path("page") String page
    ); 

    @GET("/3/gallery/album/{id}")
    Call<GalleryAlbumResponse> getGalleryAlbum(
            @Header("Authorization") String auth,
            @Path("id") String id
    );

}
