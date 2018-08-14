package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.inpix.R;

public class PresentationActivity extends Activity implements OnClickListener {
    //Acciones
    private Button btnNext;

    //Intents
    private Intent intentMainList;

    //Layouts
    private int mainLayout = R.layout.presentation;
    private TextView lblInvite;
    private TextView lblEvent;
    private static final String TAG = "INPIX-presActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mainLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        intentMainList = new Intent(PresentationActivity.this, MainListActivity.class);

        //Declaro objetos de la actividad principal.
        btnNext = (Button) findViewById(R.id.btn_pres_next);
        btnNext.setOnClickListener(this);

        lblEvent = (TextView) findViewById(R.id.txt_pres_event);
        lblInvite = (TextView) findViewById(R.id.lblInvite);
        switch (getPrefs("opCode")) {
            case "LOVE":
                lblInvite.setText(R.string.invite_take_images);
                lblEvent.setText(getPrefs("opCode"));
                break;
            case "PEACE":
                lblInvite.setText(R.string.invite_take_images);
                lblEvent.setText(getPrefs("opCode"));
                break;
            case "NATURA":
                lblInvite.setText(R.string.invite_take_images);
                lblEvent.setText(getPrefs("opCode"));
                break;
            case "INDIA":
                lblInvite.setText(R.string.invite_take_images_2);
                lblEvent.setText(getPrefs("opCode"));
                break;
            case "SMILE":
                lblInvite.setText(R.string.invite_take_images);
                lblEvent.setText("HAPPINESS");
                break;
        }


    }

    private boolean isOnline() {
        String netName = null;
        //String ipAddress = null;

        try{
            WifiManager cm;
            cm = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //    cm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            //TODO: verificar que el sistema este conectado a internet cm.getWifiState();
            netName = cm.getConnectionInfo().getSSID();
        }catch(Exception e){
            //ipAddress = null;
            Log.e(TAG, "isOnline: " + e);
        }
        if (netName != null){
            return true;
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View v) {
        // Se ejecuta al hacer click sobre algun elemento que del Activity
        int id = v.getId();
        switch (id) {
            case R.id.btn_pres_next:
                try {
                    if (isOnline()){
                        startActivity(intentMainList);
                    }else{
                        Toast tst = Toast.makeText(PresentationActivity.this, "No estas conectado a internet"+'\n' + " necesitas conectarte para poder subir fotos", Toast.LENGTH_LONG);
                        tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
                        tst.show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: " + this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }


    public String getPrefs(String pKey) {
        SharedPreferences pref = getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
        String rsta = pref.getString(pKey, "NADA");

        return rsta;
    }


}
