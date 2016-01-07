package fr.esstin.benjamin.imgurproject.displayer;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import fr.esstin.benjamin.imgurproject.R;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryAlbum;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryImage;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryParents;

/**
 * Created by Benjamin on 03/12/2015.
 */
public class ScrollingDisplayer {

    public void displayImage(View v, GalleryParents images){
        LinearLayout rT = (LinearLayout) v.findViewById(R.id.ScrollLayout);
        if (images.getClass() == GalleryAlbum.class){
            GalleryAlbum gA = (GalleryAlbum) images;
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
        else if (images.getClass() == GalleryImage.class){
            GalleryImage gI = (GalleryImage) images;
            TextView TvN = new TextView(rT.getContext());
            TvN.setText(gI.title);
            rT.addView(TvN);
            ImageView Iv = new ImageView(rT.getContext());
            Ion.with(Iv).load(gI.link);
            rT.addView(Iv);
            TextView TvND = new TextView(rT.getContext());
            TvND.setText(gI.description);
            rT.addView(TvND);
        }

    }
}
