package fr.esstin.benjamin.imgurproject.imgurModel;

import fr.esstin.benjamin.imgurproject.imgurEnum.Section;
import fr.esstin.benjamin.imgurproject.imgurEnum.Sort;
import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Benjamin on 02/12/2015.
 */
public interface ImgurAPI {
    String server = "https://api.imgur.com";

    @POST("/image")
    void postImage(
            @Header("Authorization") String auth,
            //@Query("image") TypedFile file,
            @Query("album") String albumId,
            @Query("type") String type,
            @Query("name") String name,
            @Query("title") String title,
            @Query("description") String description,
            Callback<ImageResponse> cb
    );

    @GET("/3/gallery")
      Call<GalleryResponse> getGallery(
            @Header("Authorization") String auth
    );

    @GET("/gallery/{section}/{sort}/{page}/")
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
