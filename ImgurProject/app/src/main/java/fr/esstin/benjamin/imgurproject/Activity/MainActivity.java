package fr.esstin.benjamin.imgurproject.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.esstin.benjamin.imgurproject.R;
import fr.esstin.benjamin.imgurproject.services.DownloadGallery;
import fr.esstin.benjamin.imgurproject.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TextView text;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(NetworkUtils.isConnected(this.getBaseContext())) {
            text = (TextView) findViewById(R.id.progressText);
            new DownloadGallery(text).execute();
        }
        else{
            Log.d("", "notConnected");
        }



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_scrolling, container, false);

            /*LinearLayout rT = (LinearLayout) v.findViewById(R.id.ScrollLayout);
            if (result.get(i).getClass() == GalleryAlbum.class){
                GalleryAlbum gA = (GalleryAlbum) result.get(i);
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
            else if (result.get(i).getClass() == GalleryImage.class){
                GalleryImage gI = (GalleryImage) result.get(i);
                TextView TvN = new TextView(rT.getContext());
                TvN.setText(gI.title);
                rT.addView(TvN);
                ImageView Iv = new ImageView(rT.getContext());
                Ion.with(Iv).load(gI.link);
                rT.addView(Iv);
                TextView TvND = new TextView(rT.getContext());
                TvND.setText(gI.description);
                rT.addView(TvND);
            }*/

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 60;
        }
    }
}

