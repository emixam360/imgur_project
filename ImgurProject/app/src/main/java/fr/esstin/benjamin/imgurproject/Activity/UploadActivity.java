package fr.esstin.benjamin.imgurproject.Activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.esstin.benjamin.imgurproject.Constants;
import fr.esstin.benjamin.imgurproject.R;
import fr.esstin.benjamin.imgurproject.imgurModel.GalleryImage;
import fr.esstin.benjamin.imgurproject.imgurModel.ImageResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import fr.esstin.benjamin.imgurproject.services.ServiceGenerator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static fr.esstin.benjamin.imgurproject.utils.LargeBitmapsUtil.decodeSampledBitmapFromResource;

public class UploadActivity extends AppCompatActivity {

    Button button_take, button_select,button_save,button_upload, button_write;
    EditText editText;
    ImageView imageView;
    Canvas canvas;

    TextView instruction;

    String pathorg;

    Boolean picture_selected = Boolean.FALSE;

    private static int RESULT_LOAD_IMAGE = 2;
    private static int REQUEST_TAKE_PHOTO = 3;

    int widthbitmap = 500;
    int heightbitmap = 500;


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
        instruction = (TextView) findViewById(R.id.textView);


        button_save.setVisibility(View.GONE);
        button_upload.setVisibility(View.GONE);
        button_write.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImgurAPI ImgurSrv = ServiceGenerator.createService(ImgurAPI.class);
                RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), new File(pathorg));
                Call<ImageResponse> call = ImgurSrv.postImage(
                        Constants.getClientAuth(),
                        request/*
                        "name",
                        "Image",
                        "Description"*/
                );
                call.enqueue(new Callback<ImageResponse>() {
                    @Override
                    public void onResponse(Response<ImageResponse> response, Retrofit retrofit) {
                        Log.d("",response.body().data.toString());
                        GalleryImage gI = response.body().data;
                        // id de l'image -> gI.id
                        //le lien du coup c'est http://imgur.com/{id}
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("","Fail");
                    }
                });

            }
        });


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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(Boolean.TRUE);
                    } catch (IOException ex) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                    }
                }

            }
        });

        button_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                write();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
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
            instruction.setVisibility(View.GONE);
            picture_selected = Boolean.TRUE;
        };
    };

    public Bitmap write(){
        Bitmap bitmap = decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap);
        //Bitmap bitmap = BitmapFactory.decodeFile(pathorg);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);
        canvas = new Canvas(bitmap);


        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(255, 255, 255));
        // text size in pixels
        paint.setTextSize((int) (140));


        Typeface tf =Typeface.createFromAsset(getAssets(),"impact.ttf");
        paint.setTypeface(tf);


        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(editText.getText().toString(), 0, editText.getText().toString().length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = bounds.height() * (5/4);



        canvas.drawText(editText.getText().toString(), x, y, paint);


        imageView.setImageBitmap(bitmap);
        return bitmap;
    };

    public void save() throws IOException {
        File photoFile = null;
        try {
            photoFile = createImageFile(Boolean.FALSE);
        } catch (IOException ex) {
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Bitmap b = write();
            FileOutputStream fOut = new FileOutputStream(photoFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        }

    };


    private File createImageFile(Boolean selected) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HH:mm:ss").format(new Date());
        String imageFileName = "imgurproject_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if(selected == Boolean.TRUE) {
            pathorg = image.getAbsolutePath();
        };

        return image;
    }

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
            pathorg = cursor.getString(columnIndex);
            cursor.close();

            setPicture_selected();
            //bitmap = BitmapFactory.decodeFile(picturePath);
            //imageView.setImageBitmap(bitmap);
            imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap));
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            setPicture_selected();
            //imageView.setImageBitmap(bitmap);
            imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap));

/*
            Toast.makeText(this, "Image enregistrée dans " + getApplicationContext().getString(R.string.foldername),
                    Toast.LENGTH_LONG).show();
*/
        }
    }
}
