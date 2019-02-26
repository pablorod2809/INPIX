package com.lightbox.android.inpix.events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.addEventResponse;
import com.lightbox.android.inpix.util.MyFiles;
import com.lightbox.android.inpix.activities.util.PreferenceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEvent2Activity extends AppCompatActivity implements Callback<addEventResponse> {

    private RadioGroup visibility;
    private static final String TAG = "INPIX-GroupActivityVis";
    private Button btnNext;
    private Button btnBack;
    private Bitmap imageToSend;
    private String txtName;
    private String txtTitle;
    private Bitmap bmpBackground;
    private String txtLocalConf;
    private String txtEmailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_new_step2);

        //obtengo datos cargados en el paso 1
        Bundle bundle = this.getIntent().getExtras();
        txtName = bundle.getString("evt_name");
        txtTitle = bundle.getString("evt_title");
        final String imgBackGround = bundle.getString("background");
        bmpBackground = BitmapFactory.decodeFile(imgBackGround); //.decodeByteArray(imgBackGround, 0, imgBackGround.length);

        //obtengo detos desde preferences.
        PreferenceManager pref = new PreferenceManager();
        txtEmailUser = pref.getPrefString("opEmail", this);
        txtLocalConf = pref.getPrefString("opLocale", this);


        //Obtengo datos de PRIVADO O PUBLICO y PASSWORD
        /* TODO: Ver como cargamos una clave que permita al usuario que creeo el grupo acceder a administrarlo en un futuro,
                 habia pensado el IMEI pero hay que pedir permisos y me hace ruido.
                 talvez pedir al usuario la carga de una password de administrador, que será enviada a su email.*/
        btnNext = findViewById(R.id.btnNewGroup2_Next);
        btnBack = findViewById(R.id.btnNewGroup2_Back);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: llamar a sendEvent, aca agregar cada uno de los campos capturados.
                sendNewEvent(txtName,txtName,txtTitle,txtLocalConf,"PRI",txtEmailUser,"abc123",bmpBackground);
                Intent nextStepIntent = new Intent(AddEvent2Activity.this, AddEventResultActivity.class);
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


    private void sendNewEvent(String pEventName, String pEventTitle, String pEventDescription, String pEventLanguage,
                              String pEventVisibility, String pUserEmail, String pUserPass,  Bitmap pBackground){
        if (pBackground == null){
            Log.i(TAG, "addEventNew. No esta recibiendo imagen de fondo ");
            imageToSend = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        } else {
            imageToSend = pBackground;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageToSend.compress(Bitmap.CompressFormat.PNG, 32, stream);
        InputStream in = new ByteArrayInputStream(stream.toByteArray());
        String fileName    = "background.png";
        File file = MyFiles.inputStreamToFile(in, fileName, AddEvent2Activity.this.getBaseContext());

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("eventBackground", file.getName(), reqFile);
        RequestBody eventName = RequestBody.create(MediaType.parse("text/plain"), pEventName);
        RequestBody eventTitle = RequestBody.create(MediaType.parse("text/plain"), pEventTitle);
        RequestBody eventDescription = RequestBody.create(MediaType.parse("text/plain"), pEventDescription);
        RequestBody eventLanguage = RequestBody.create(MediaType.parse("text/plain"), pEventLanguage);
        RequestBody eventVisibility = RequestBody.create(MediaType.parse("text/plain"), pEventVisibility);
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), pUserEmail);
        RequestBody userPassword = RequestBody.create(MediaType.parse("text/plain"), pUserPass);
        try{
           Call<addEventResponse> call = InpixApiAdapter.getApiService().addEvent(body,eventName,eventTitle,eventDescription,eventLanguage,eventVisibility,userEmail,userPassword);
           call.enqueue(AddEvent2Activity.this);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(Call<addEventResponse> call, Response<addEventResponse> response) {
        //TODO: Respuesta de llamado al addEvent
        Log.i(TAG,response.body().getEventCode());
    }

    @Override
    public void onFailure(Call<addEventResponse> call, Throwable t) {
        //TODO: Respuesta de llamado al addEvent

    }
}

