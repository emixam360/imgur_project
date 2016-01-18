package fr.esstin.benjamin.imgurproject.Activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private ImageView loading;
    private TextView ltext;

    private ViewPager mViewPager;

    private ArrayList<GalleryParents> FrontPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = (ImageView) findViewById(R.id.GifLoader);
        ltext = (TextView) findViewById(R.id.TextLoader);

        downloadGal();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    public void downloadGal(){
        if(NetworkUtils.isConnected(this.getBaseContext())) {
            new DownloadGallery().execute();
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Network fail")
                    .setMessage("Please turn on internet on your device")
                            //premier bouton copier coller
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            downloadGal();
                        }
                    })
                    .show();
        }
    }

    public class DownloadGallery extends AsyncTask<Void, View, ArrayList<GalleryParents>> {

        public DownloadGallery(){
        }

        @Override
        protected ArrayList<GalleryParents> doInBackground(Void... params) {

            publishProgress(loading, ltext);

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
            loading.setVisibility(View.INVISIBLE);
            ltext.setVisibility(View.INVISIBLE);
            Log.d("",gallery.toString());

            MyAdapter = new SectionsPagerAdapter(gallery.size(), getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(MyAdapter);
        }

        @Override
        protected void onProgressUpdate(View... gif){
            Glide.with(getBaseContext())
                    .load(R.mipmap.giphy)
                    .dontTransform()
                    .into((ImageView) gif[0]);
            ((TextView) gif[1]).setText("Chargement ...");
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

                if (gA.title != null) {
                    TextView Tv = new TextView(getContext());
                    Tv.setText(gA.title);
                    Tv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    Tv.setTextSize(18);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0, 0, 0, 16);
                    Tv.setLayoutParams(params1);
                    rT.addView(Tv);
                }

                for (GalleryImage I: gA.images) {
                    if(I.title != null){
                        TextView TvN = new TextView(getContext());
                        TvN.setText(I.title);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(0, 0, 0, 16);
                        TvN.setLayoutParams(params2);
                        rT.addView(TvN);
                    }

                    ImageView Iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params3  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params3.setMargins(0, 0, 0, 16);
                    Iv.setLayoutParams(params3);
                    rT.addView(Iv);

                    if(I.type.equals(Constants.GIF)){
                        Glide.with(getContext())
                                .load(I.link)
                                .asGif()
                                .dontTransform()
                                .into(Iv);

                    }else{
                        Glide.with(getContext())
                                .load(I.link)
                                .asBitmap()
                                .fitCenter()
                                .into(Iv);
                    }

                    if (I.description != null) {
                        TextView TvND = new TextView(getContext());
                        TvND.setText(I.description);
                        LinearLayout.LayoutParams params4  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params4.setMargins(0, 0, 0, 16);
                        TvND.setLayoutParams(params4);
                        rT.addView(TvND);
                    }
                }
            }
            else {
                if (gP.getClass() == GalleryImage.class) {
                    GalleryImage gI = (GalleryImage) gP;

                    if (gI.title != null) {
                        TextView TvN = new TextView(getContext());
                        TvN.setText(gI.title);
                        TvN.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                        TvN.setTextSize(18);
                        LinearLayout.LayoutParams params1  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(0, 0, 0, 16);
                        TvN.setLayoutParams(params1);
                        rT.addView(TvN);
                    }

                    ImageView Iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params2  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(0, 0, 0, 16);
                    Iv.setLayoutParams(params2);
                    rT.addView(Iv);
                    if (gI.type.equals(Constants.GIF)) {
                        Glide.with(getContext())
                                .load(gI.link)
                                .asGif()
                                .dontTransform()
                                .into(Iv);
                    } else {
                        Glide.with(getContext())
                                .load(gI.link)
                                .fitCenter()
                                .into(Iv);
                    }

                    if (gI.description != null) {
                        TextView TvND = new TextView(getContext());
                        TvND.setText(gI.description);
                        LinearLayout.LayoutParams params3  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.setMargins(0, 0, 0, 16);
                        TvND.setLayoutParams(params3);
                        rT.addView(TvND);
                    }
                }
            }

            return rootView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

