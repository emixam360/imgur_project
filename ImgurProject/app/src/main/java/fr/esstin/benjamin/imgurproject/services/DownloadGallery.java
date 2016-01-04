package fr.esstin.benjamin.imgurproject.services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import fr.esstin.benjamin.imgurproject.Constants;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbum;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbumResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryConverter;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryParents;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Benjamin on 02/12/2015.
 */
public class DownloadGallery extends AsyncTask<Void, String, ArrayList<GalleryParents>> {

    private TextView text;

    public DownloadGallery(TextView text) {
        this.text = text;
    }

    @Override
    protected ArrayList<GalleryParents> doInBackground(Void... params) {
        publishProgress("Téléchargement en cours");

        ArrayList<GalleryParents> FrontPage = new ArrayList<>();

        ImgurAPI ImgurSrv = ServiceGenerator.createService(ImgurAPI.class);
        Call<GalleryResponse> call = ImgurSrv.getGallery(
                Constants.getClientAuth()
        );

        try {
            FrontPage = new GalleryConverter().convertGallery(call.execute().body().data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int j = 0; j < FrontPage.size(); j++) {
            if (FrontPage.get(j).getClass() == GalleryAlbum.class) {
                GalleryAlbum gA = (GalleryAlbum) FrontPage.get(j);
                Call<GalleryAlbumResponse> call2 = ImgurSrv.getGalleryAlbum(
                        Constants.getClientAuth(),
                        gA.id
                );
                try {
                    FrontPage.set(j, new GalleryConverter().convertAlbum(call2.execute().body().data));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return FrontPage;
    }

    @Override
    protected void onPostExecute(ArrayList<GalleryParents> gallery) {
        text.setVisibility(View.INVISIBLE);
        Log.d("",gallery.toString());
    }

    @Override
    protected void onProgressUpdate(String... progressTest){
        text.setText(progressTest[0]);
    }

}
