package fr.esstin.benjamin.imgurproject.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.esstin.benjamin.imgurproject.Constants;
import fr.esstin.benjamin.imgurproject.displayer.ScrollingDisplayer;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbum;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbumResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryConverter;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryParents;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import fr.esstin.benjamin.imgurproject.utils.NetworkUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Benjamin on 02/12/2015.
 */
public class GalleryService extends AsyncTask<Integer, Integer, ArrayList<GalleryParents>>{

    private ImgurAPI service;

    public ArrayList<GalleryParents> FrontPage;

    public ScrollingDisplayer sD;

    private WeakReference<Context> mContext;

    public GalleryService(Context context) {
        this.mContext = new WeakReference<>(context);
        this.sD = new ScrollingDisplayer();
        this.service = buildRestAdapter().create(ImgurAPI.class);
        this.FrontPage = new ArrayList<>();
    }

    @Override
    public ArrayList<GalleryParents> doInBackground(Integer... params) {

        if (!NetworkUtils.isConnected(mContext.get())) {
            return null;
        }

        Call<GalleryResponse> getGallery = service.getGallery(
                Constants.getClientAuth()
        );

        getGallery.enqueue(new Callback<GalleryResponse>() {
            @Override
            public void onResponse(Response<GalleryResponse> response, Retrofit retrofit) {
                FrontPage = new GalleryConverter().convertGallery(response.body().data);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("", "onFailure: " + t.toString());
            }
        });

        for (int j = 0;j<FrontPage.size();j++) {
            final int J = j;
            if (FrontPage.get(j).getClass() == GalleryAlbum.class) {
                GalleryAlbum gA = (GalleryAlbum) FrontPage.get(j);
                Call<GalleryAlbumResponse> getAlbum = service.getGalleryAlbum(
                        Constants.getClientAuth(),
                        gA.id
                );
                getAlbum.enqueue(new Callback<GalleryAlbumResponse>() {
                    @Override
                    public void onResponse(Response<GalleryAlbumResponse> response, Retrofit retrofit) {
                        FrontPage.set(J, new GalleryConverter().convertAlbum(response.body().data));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("", "onFailure: " + t.toString());
                    }
                });
            }
        }
        return FrontPage;
    }

    public void onPostExecute(View v, int i, ArrayList<GalleryParents> result) {
        this.sD.displayImage(v,result.get(i));
    }

    private Retrofit buildRestAdapter() {
        Retrofit imgurAdapter = new Retrofit.Builder()
                .baseUrl(ImgurAPI.server)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return imgurAdapter;
    }

}
