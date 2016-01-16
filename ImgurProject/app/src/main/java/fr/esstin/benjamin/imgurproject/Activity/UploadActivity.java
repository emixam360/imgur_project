package fr.esstin.benjamin.imgurproject.Activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


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
import fr.esstin.benjamin.imgurproject.imgurModel.ImageResponse;
import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import fr.esstin.benjamin.imgurproject.services.ServiceGenerator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static fr.esstin.benjamin.imgurproject.utils.LargeBitmapsUtil.decodeSampledBitmapFromResource;

public class UploadActivity extends AppCompatActivity {

    Button button_take, button_select,button_save,button_upload, button_write; //boutons de l'interface
    EditText editText; //zone de texte
    ImageView imageView;
    Canvas canvas; //canvas pour l'écriture de l'image
    TextView instruction;
    String pathorg, pathmod, link; //pathorg, lien vers l'image selectionné, link lien vers l'image uploadé, pathmod lien vers l'image écrite
    Boolean picture_selected = Boolean.FALSE; //passe à true une fois qu'une image a été selectionnée

    private static int RESULT_LOAD_IMAGE = 2;
    private static int REQUEST_TAKE_PHOTO = 3;

    //qualité max des images
    int widthbitmap = 500;
    int heightbitmap = 500;

    //ON CREATE
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

        //upload d'une image
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        //lancement de l'activité gallerie photo
        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        //lancement de l'activité appareil photo
        button_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Création du fichier
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

        //ecriture du texte et affichage dans l'image view
        button_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                write();
            }
        });

        //sauvegarde en local
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
    }

    //FONCTIONS

    //une fois qu'une image est selectionnée, modification de l'interface
    public void setPicture_selected(){
        if (picture_selected==Boolean.FALSE){
            button_save.setVisibility(View.VISIBLE);
            button_upload.setVisibility(View.VISIBLE);
            button_write.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            instruction.setVisibility(View.GONE);
            picture_selected = Boolean.TRUE;
        }
    }

    //ecriture de texte sur l'image
    public Bitmap write(){
        //Bitmap bitmap = BitmapFactory.decodeFile(pathorg); //image non reduite
        Bitmap bitmap = decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888; //rend la bitmap modifiable
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextSize(140);

        Typeface tf =Typeface.createFromAsset(getAssets(),"impact.ttf"); //definition de la police
        paint.setTypeface(tf);

        Rect bounds = new Rect();
        paint.getTextBounds(editText.getText().toString(), 0, editText.getText().toString().length(), bounds); //mesure de la taille du texte
        //position du texte en haut au milieu
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = bounds.height() * (5/4);

        canvas.drawText(editText.getText().toString(), x, y, paint);

        imageView.setImageBitmap(bitmap); //affichage
        return bitmap;
    }

    //sauvegarde de l'image
    public void save() throws IOException {
        File photoFile = null;
        try {
            photoFile = createImageFile(Boolean.FALSE); //creation d'un fichier
        } catch (IOException ex) {
            Log.d("","Fail");
        }
        if (photoFile != null) {
            Bitmap b = write(); //ecriture du texte sur l'image
            FileOutputStream fOut = new FileOutputStream(photoFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            pathmod = photoFile.getAbsolutePath();
            Toast.makeText(this, R.string.saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    //création d'un fichier où placer une image
    private File createImageFile(Boolean selected) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = R.string.app_name +"_"+ timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES); //emplacement dans le dossier des images
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //si image sélectionnée via la galerie
        if(selected == Boolean.TRUE) {
            pathorg = image.getAbsolutePath();
        }
        return image;
    }

    //upload de l'image sur imgur
    public void upload(){
        ImgurAPI ImgurSrv = ServiceGenerator.createService(ImgurAPI.class);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), new File(pathmod));
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
                Log.d("", response.body().data.toString());
                //GalleryImage gI = response.body().data;
                link= response.body().data.link;

                //pop up apres upload
                new AlertDialog.Builder(UploadActivity.this)
                        .setTitle("URL")
                        .setMessage(link)
                                //premier bouton copier coller
                        .setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", link);
                                clipboard.setPrimaryClip(clip);
                            }
                        })
                                //deuxième bouton partage
                        .setNegativeButton(R.string.share, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                            }
                        })
                        .show();
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("","Fail");
            }
        });
    }


    //Actiions après la selection d'image dans la galerie ou la prise de photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //photo via la gallerie
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pathorg = cursor.getString(columnIndex);
            cursor.close();

            //changement interface
            setPicture_selected();

            //imageView.setImageBitmap(bitmap); ::code image non réduite
            imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap));
        }

        //photo prise avec l'appareil
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //changement interface
            setPicture_selected();

            //imageView.setImageBitmap(bitmap); //code image non réduite
            imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(pathorg, widthbitmap, heightbitmap));

            Toast.makeText(this, R.string.saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}
