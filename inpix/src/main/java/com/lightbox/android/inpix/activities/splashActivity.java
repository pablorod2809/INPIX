package com.lightbox.android.inpix.activities;

import com.lightbox.android.inpix.activities.util.SystemUiHider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.provider.Settings;
import android.widget.Toast;

import com.lightbox.android.inpix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class splashActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int MY_PERMISSION_CAMERA = 9998; //este valor es


    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private Timer timer;
    private TimerTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        final View imv = findViewById(R.id.logoSplash);

       // final View contentView = findViewById(R.id.spash_screen);
        final View imvfondo = findViewById(R.id.imageView4);
        imvfondo.setVisibility(View.VISIBLE);
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.spash_logo);
        imv.startAnimation(logoMoveAnimation);

        task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent();
                String myName = getPrefs("opName");
                if (!myName.equals("NADA")) {
                    String dateFrom = getPrefs("opEvtFrom");
                    String dateTo = getPrefs("opEvtTo");
                    String now = formatDate(new Date());
                    int rsta =compareDates(dateFrom, now );
                    rsta = rsta * compareDates(now, dateTo);
                    String activeCode = getPrefs("opCode");
                    if (!activeCode.equals("NADA")&& rsta > 0 ){
                        //Usuario y codigo activo, llamar a pantalla principal para toma de fotos.
                        //mainIntent.setClass(splashActivity.this, MainActivity.class);
                        mainIntent.setClass(splashActivity.this, MainListActivity.class);
                    } else {
                        //USuario dado de alta pero no tiene un codigo activo. Llamar pantalla codigo
                        //mainIntent.setClass(splashActivity.this, CodeActivity.class);
                        mainIntent.setClass(splashActivity.this, EventListActivity.class);
                    }
                } else {
                    //No hay usuario activo, llamar a pantalla de usuario
                    mainIntent.setClass(splashActivity.this, UserActivity.class);
                }
                startActivity(mainIntent);
                //splashActivity.this.checkPermission();
                finish();
            }
        };
        timer = new Timer();

        this.checkPermission();

    }

    public String getPrefs(String pKey) {
        SharedPreferences pref = getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
        String rsta = pref.getString(pKey, "NADA");
        return rsta;
    }

    private int compareDates(String pDate1, String pDate2) {
        int rsta = 0;
        try {
            SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fechaDate1 = formateador.parse(pDate1);
            Date fechaDate2 = formateador.parse(pDate2);
            if (fechaDate1.before(fechaDate2)) {
                rsta = 1;  //Fecha1 es menor que fecha 2
            } else if (fechaDate2.before(fechaDate1)){
                rsta = -1; //Fecha2 es menor que fecha 1
            } else {
                rsta = 0;  //Las dos fechas son iguales.
            }
        } catch (ParseException e) {
            System.out.println("Se Produjo un Error!!!  " + e.getMessage());
        }
        return rsta;
    }

    private String formatDate(Date pDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateFormat.format(pDate);
        return datetime;
    }

    /*************************************************
     * MANEJO DE PERMISOS PARA ANDROID 7 o posterior.
     ************************************************/
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            //int hasFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED || hasStoragePermission != PackageManager.PERMISSION_GRANTED ||
                    hasLocationPermission != PackageManager.PERMISSION_GRANTED){
                    //|| hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_SETTINGS}, MY_PERMISSION_CAMERA);
            } else {
                timer.schedule(task, 4000);
                return;
            }
        }else {
            timer.schedule(task, 6000);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(MY_PERMISSION_CAMERA == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sin permiso para acceder a la camara ", Toast.LENGTH_LONG).show();
            }

            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sin permiso para acceder al disco ", Toast.LENGTH_LONG).show();
            }
            if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sin permiso para acceder gps ", Toast.LENGTH_LONG).show();
            } else {
                this.timer.schedule(task, 4000);
            }

        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
