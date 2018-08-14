package com.lightbox.android.inpix.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.inpix.CameraApplication;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.util.InpixEventAdapter;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.eventListResponse;
import com.lightbox.android.inpix.io.responses.eventResponse;

import java.util.ArrayList;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pablorodriguez on 18/6/18.
 */

public class EventListActivity extends AppCompatActivity implements View.OnClickListener, Callback<eventListResponse>, AbsListView.OnScrollListener {

    private int mainLayout = R.layout.activity_events_img;
    private Intent intentPresentation;
    private Intent intentCode;
    private Intent intentMainList;

   // private TextView title;
    private GridView grid;
    private ArrayList<eventResponse> listImages;
    private InpixEventAdapter ipxAdapter;
    private TextView mainTitle;

    private static final String TAG = "INPIX-EventListActivity";
    private final String BASE = "https://inpix.online/private/";
    private int lastPage = 0;
    private boolean mLoading = false;
    private String lang = "EN";

    //private ProgressBar progressBar;
    private SpotsDialog dialog;
    private CameraApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Pongo Dialogo de espera
        dialog = new SpotsDialog(this);
        dialog.show();
        mLoading = true;

        //Seteo Layout a utilizar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mainLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Actualizo fuente para texto de pantalla
        application = (CameraApplication) getApplication();

        //Seteo Titulo
        mainTitle = (TextView) findViewById(R.id.txtView);
        application.setTypeface(mainTitle);

        //Listeners de Botones

        //btnClock = (ImageButton) findViewById(R.id.btnClock);
        //btnClock.setOnClickListener(this);


        //Preparo la Grilla
        grid = (GridView) findViewById(R.id.grid1);
        listImages = new ArrayList<eventResponse>();
        lang = Locale.getDefault().getLanguage().toUpperCase();
        ipxAdapter = new InpixEventAdapter(this, new ArrayList<eventResponse>(listImages), lang, BASE);
        grid.setAdapter(ipxAdapter);
        grid.setOnScrollListener(this);

        //Seteo Intents
        intentMainList = new Intent(EventListActivity.this, MainListActivity.class);
        intentCode = new Intent(EventListActivity.this, EventActivity.class);
        intentPresentation = new Intent(EventListActivity.this, PresentationActivity.class);

        try{
            Call<eventListResponse> call = InpixApiAdapter.getApiService().listEvents(Integer.toString(lastPage),lang);
            call.enqueue(EventListActivity.this);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public CameraApplication getApp (){
        return this.application;
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnBack:
                try {
                    if (isOnline()) {
                        startActivity(intentCode);
                    } else {
                        Toast tst = Toast.makeText(EventListActivity.this, getString(R.string.activities_main_no_connect), Toast.LENGTH_LONG);
                        tst.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        tst.show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }
                break;
        }
    };

    @Override
    public void onResponse(Call<eventListResponse> call, Response<eventListResponse> response) {
        eventListResponse rs = response.body();
        try {
            if (lastPage == 0)
                listImages.clear();

            if (rs.getList()!= null){
                if (rs.getList().size()>0){
                    listImages.addAll(rs.getList());
                    ipxAdapter.refresh(listImages);
                }
            } else
                lastPage = -1;

        } catch (Exception e){
            e.printStackTrace();
        }
        mLoading = false;

        try {
            dialog.dismiss();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<eventListResponse> call, Throwable t) {
        mLoading = false;
        try {
            dialog.dismiss();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private boolean isOnline() {
        String netName = null;

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
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem+visibleItemCount == totalItemCount && !mLoading && totalItemCount>0){
            //String valor = "firstVisible:" + firstVisibleItem + " -visibleCount:" + visibleItemCount + "total: " + totalItemCount;
            //Log.i("log-scroll", valor);
            mLoading = true;
            if (lastPage >= 0) {
                try {
                    lastPage++;
                    Call<eventListResponse> call;
                    call = InpixApiAdapter.getApiService().listEvents(Integer.toString(lastPage), lang);
                    call.enqueue(EventListActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }
            }
        }
    }

}
