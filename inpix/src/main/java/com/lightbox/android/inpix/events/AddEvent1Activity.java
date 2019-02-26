package com.lightbox.android.inpix.events;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.util.PreferenceManager;
import com.squareup.picasso.Picasso;

public class AddEvent1Activity extends AppCompatActivity{

    private String title;
    private String description;
    private String backgroundPath;
    private String visibility;
    private String pathBackground;

    private ImageView photoUpload;
    private TextView tvTitle;
    private TextView  tvDescription;
    private ImageView imageBackground;
    private ImageView fondo;
    private Button btnNext;
    private Button btnBack;
    private PreferenceManager pref;

    private static final int REQUEST_CAMERA = 55;
    private static final int SELECT_FILE = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_new_step1);
        photoUpload = findViewById(R.id.photoUpload);
        tvTitle = findViewById(R.id.txtTitle);
        tvDescription = findViewById(R.id.txtDescription);
        imageBackground = findViewById(R.id.imageBackground);
        fondo = findViewById(R.id.imageBackground2);
        btnNext = findViewById(R.id.btnNewGroup1_Next);
        btnBack = findViewById(R.id.btnNewGroup1_Back);


        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Camara", "Desde Galeria", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent1Activity.this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Agregar foto desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Camara")) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            startActivityForResult(cameraIntent, REQUEST_CAMERA);
                        } else if (options[item].equals("Desde Galeria")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, SELECT_FILE);
                        } else if (options[item].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStepIntent = new Intent(AddEvent1Activity.this, AddEvent2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("background", AddEvent1Activity.this.pathBackground);
                bundle.putString("evt_title",AddEvent1Activity.this.tvDescription.getText().toString());
                bundle.putString("evt_name",AddEvent1Activity.this.tvTitle.getText().toString());
                nextStepIntent.putExtras(bundle);
                startActivity(nextStepIntent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // Se ejecuta cuando vuelve de la actividad de la camara o de la actividad de la galeria
    // resuelve que hacer con la imagen seleccionada o capturada.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fondo.setVisibility(View.VISIBLE);
        if (REQUEST_CAMERA == requestCode && resultCode == RESULT_OK) { //Si lo recibo de la camara
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            }
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", null);
            File filesDir = this.getFilesDir();
            File imageFile = new File(filesDir, "image" + ".jpg");
            OutputStream os;
            try {
                os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }
            Uri.parse(path);
            this.pathBackground = path;
            Picasso.get().load(path).noPlaceholder().centerCrop().fit().into(imageBackground);
            imageBackground.setAlpha(new Float(0.5));
        }

        if (requestCode == SELECT_FILE) { //Si viene del filesystem
            Toast.makeText(this, "Gallery clicked", Toast.LENGTH_SHORT).show();
            Uri selectedImageURI = data.getData();
            String filePath = getRealPathFromURIPath(selectedImageURI, this);
            File file = new File(filePath);
            //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            try {
                InputStream inputStream = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024 * 8];
                int bytesRead = 0;

                while ((bytesRead = inputStream.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.pathBackground = selectedImageURI.getPath();
            Picasso.get().load(selectedImageURI).noPlaceholder().centerCrop().fit().into((imageBackground));
            imageBackground.setAlpha(new Float(0.5));
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


}

