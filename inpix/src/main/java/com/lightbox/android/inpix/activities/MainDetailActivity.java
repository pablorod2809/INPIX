package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.inpix.R;
import com.squareup.picasso.Picasso;

public class MainDetailActivity extends Activity implements OnClickListener{
    //Acciones
    private Button btnNext;

    //Intents
    private Intent intentMainList;

    //Layouts
    private int mainLayout = R.layout.activity_main_det;
    private ImageView imgPreview;
    private ImageView imgLogo;
    private static final String TAG = "INPIX-detailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mainLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String url = getPrefs("url");

        //Declaro objetos de la actividad principal.
        imgPreview = (ImageView) findViewById(R.id.imgDetail);
        Picasso.get().load(url)
                .fit()
                .centerCrop()
                .tag(this)
                .into(imgPreview);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);

        imgPreview.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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

    @Override
    public void onClick(View view) {
        this.finish();
    }
}
