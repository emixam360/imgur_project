package fr.esstin.benjamin.imgurproject.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.util.ArrayList;

import fr.esstin.benjamin.imgurproject.Constants;
import fr.esstin.benjamin.imgurproject.R;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbum;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbumResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryConverter;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryImage;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryParents;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import fr.esstin.benjamin.imgurproject.services.ServiceGenerator;
import fr.esstin.benjamin.imgurproject.utils.NetworkUtils;
import retrofit.Call;

public class MainActivity extends AppCompatActivity {

    private FragmentStatePagerAdapter MyAdapter;
    private TextView text;

    private ViewPager mViewPager;

    private ArrayList<GalleryParents> FrontPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.progressText);

        if(NetworkUtils.isConnected(this.getBaseContext())) {
            new DownloadGallery().execute();
        }
        else{
            Log.d("", "notConnected");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

    }

    public class DownloadGallery extends AsyncTask<Void, String, ArrayList<GalleryParents>> {

        public DownloadGallery(){
        }

        @Override
        protected ArrayList<GalleryParents> doInBackground(Void... params) {
            publishProgress("Téléchargement en cours");

            FrontPage = new ArrayList<>();

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

            MyAdapter = new SectionsPagerAdapter(gallery.size(), getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(MyAdapter);
        }

        @Override
        protected void onProgressUpdate(String... progressTest){
            text.setText(progressTest[0]);
        }

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter  {

        public int size;

        public SectionsPagerAdapter(int size, FragmentManager fm) {
            super(fm);
            this.size = size;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position, FrontPage.get(position));
        }

        @Override
        public int getCount() {
            return size;
        }
    }

    public static class ArrayListFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_GALLERY = "gallery";

        public ArrayListFragment() {
        }

        public static ArrayListFragment newInstance(int sectionNumber, GalleryParents gallery) {
            ArrayListFragment fragment = new ArrayListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_GALLERY, gallery);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_scrolling, container, false);
            GalleryParents gP = (GalleryParents) getArguments().getSerializable(ARG_GALLERY);

            LinearLayout rT = (LinearLayout) rootView.findViewById(R.id.ScrollLayout);
            if (gP.getClass() == GalleryAlbum.class){
                GalleryAlbum gA = (GalleryAlbum) gP;
                TextView Tv = new TextView(rT.getContext());
                Tv.setText(gA.title);
                rT.addView(Tv);
                for (GalleryImage I: gA.images) {
                    TextView TvN = new TextView(rT.getContext());
                    TvN.setText(I.title);
                    rT.addView(TvN);
                    ImageView Iv = new ImageView(rT.getContext());
                    Ion.with(Iv).load(I.link);
                    rT.addView(Iv);
                    TextView TvND = new TextView(rT.getContext());
                    TvND.setText(I.description);
                    rT.addView(TvND);
                }
            }
            else if (gP.getClass() == GalleryImage.class){
                GalleryImage gI = (GalleryImage) gP;

                TextView t = new TextView(rT.getContext());
                t.setText(gI.toString());
                rT.addView(t);

                if(gI.title != null){
                    TextView TvN = new TextView(rT.getContext());
                    TvN.setText(gI.title);
                    rT.addView(TvN);
                }
                if(gI.animated == true){

                }else{

                }
                ImageView Iv = new ImageView(rT.getContext());
                Ion.with(Iv).load(gI.link);
                rT.addView(Iv);

                if(gI.description != null) {
                    TextView TvND = new TextView(rT.getContext());
                    TvND.setText(gI.description);
                    rT.addView(TvND);
                }
            }

            return rootView;
        }
    }
}

