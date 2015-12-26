package fr.esstin.benjamin.imgurproject.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.esstin.benjamin.imgurproject.R;

public class UploadActivity extends AppCompatActivity {

    Button button_take, button_select,button_save,button_upload, button_write;
    EditText editText;
    ImageView imageView;

    Boolean picture_selected = Boolean.FALSE;

    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        button_take = (Button) findViewById(R.id.button_take);
        button_select= (Button) findViewById(R.id.button_select);
        button_save = (Button) findViewById(R.id.button_save);
        button_upload = (Button) findViewById(R.id.button_upload);
        button_write = (Button) findViewById(R.id.button_write);
        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);


        button_save.setVisibility(View.GONE);
        button_upload.setVisibility(View.GONE);
        button_write.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        button_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    };

    public void setPicture_selected(){
        if (picture_selected==Boolean.FALSE){
            button_save.setVisibility(View.VISIBLE);
            button_upload.setVisibility(View.VISIBLE);
            button_write.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            picture_selected = Boolean.TRUE;
        };
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            setPicture_selected();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            setPicture_selected();
            imageView.setImageBitmap(imageBitmap);

/*
            Toast.makeText(this, "Image enregistrée dans " + getApplicationContext().getString(R.string.foldername),
                    Toast.LENGTH_LONG).show();
*/
        }
    }
}
