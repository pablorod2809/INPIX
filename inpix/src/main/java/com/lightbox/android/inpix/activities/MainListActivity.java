package com.lightbox.android.inpix.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lightbox.android.inpix.CameraApplication;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.util.InpixImageAdapter;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.messageResponse;
import com.lightbox.android.inpix.io.responses.rankingResponse;

import java.util.ArrayList;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pablorodriguez on 18/6/18.
 */

public class MainListActivity extends AppCompatActivity implements View.OnClickListener, Callback<rankingResponse>, AbsListView.OnScrollListener {

    private int mainLayout = R.layout.activity_main_img;
    private Intent intentPhoto;
    private Intent intentCode;
   // private TextView title;
    private GridView grid;
    private ImageButton btnPhoto;
    private ImageButton btnBack;

    private TabLayout tabs;
    private ArrayList<rankingResponse.imageRankValue> listImages;
    private InpixImageAdapter ipxAdapter;
    private TextView mainTitle;

    private static final String TAG = "INPIX-mainListActivity";
    //private final String BASE = "https://inpix.online/private/";
    private String event;
    private String user;
    private int lastPage = 0;
    private boolean mLoading = false;
    private String status = "rank";

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

        //Obtengo evento y usuario
        SharedPreferences pref = this.getBaseContext().getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
        user = pref.getString("opUserId",pref.getString("opEmail","NULL"));
        event = pref.getString("opCode","NULL");

        //Actualizo fuente para texto de pantalla
        application = (CameraApplication) getApplication();

        //Seteo Titulo
        mainTitle = (TextView) findViewById(R.id.txtView);
        application.setTypeface(mainTitle);

        //Listeners de Botones
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnPhoto = (ImageButton) findViewById(R.id.btnAddPhoto);

        tabs = (TabLayout) findViewById(R.id.mainActivity_tabs);

        btnPhoto.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try{
                    if (tab.getPosition() == 0) {
                            lastPage = 0;
                            status = "rank";
                            mLoading = true;
                            dialog.show();
                            Call<rankingResponse> call = InpixApiAdapter.getApiService().ranking(event, Integer.toString(lastPage), user);
                            call.enqueue(MainListActivity.this);
                    }else if (tab.getPosition() == 1){
                            lastPage = 0;
                            status = "clock";
                            mLoading = true;
                            dialog.show();
                            Call<rankingResponse> call = InpixApiAdapter.getApiService().lastpics(event,Integer.toString(lastPage),user);
                            call.enqueue(MainListActivity.this);
                    }else{
                            lastPage = 0;
                            status = "own";
                            mLoading = true;
                            dialog.show();
                            Call<rankingResponse> call = InpixApiAdapter.getApiService().mypics(event,Integer.toString(lastPage),user);
                            call.enqueue(MainListActivity.this);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Preparo la Grilla
        grid = (GridView) findViewById(R.id.grid1);
        listImages = new ArrayList<rankingResponse.imageRankValue>();
        ipxAdapter = new InpixImageAdapter(this, new ArrayList<rankingResponse.imageRankValue>(listImages), InpixApiAdapter.baseUrl + event);
        grid.setAdapter(ipxAdapter);
        grid.setOnScrollListener(this);

        //Seteo Intents
        intentPhoto = new Intent(MainListActivity.this, Camera.class);
        intentCode = new Intent(MainListActivity.this, EventListActivity.class);

        try {
            Call<messageResponse> callTit = InpixApiAdapter.getApiService().messages(event, Locale.getDefault().getLanguage().toUpperCase());
            callTit.enqueue(new Callback<messageResponse>(){
                @Override
                public void onResponse(Call<messageResponse> call, Response<messageResponse> response) {
                    try{
                        messageResponse rs = response.body();
                        mainTitle.setText(rs.getTitle());
                    }catch (Exception e){
                        mainTitle.setText(getResources().getString(R.string.vote_or_share) + " " + event);
                    }
                }
                @Override
                public void onFailure(Call<messageResponse> call, Throwable t) {
                    mainTitle.setText(getResources().getString(R.string.vote_or_share) + " " + event);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            Call<rankingResponse> call = InpixApiAdapter.getApiService().ranking(event, Integer.toString(lastPage) ,user);
            call.enqueue(MainListActivity.this);
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
            case R.id.btnAddPhoto:
                try {
                    if (isOnline()) {
                        startActivity(intentPhoto);
                    } else {
                        Toast tst = Toast.makeText(MainListActivity.this, getString(R.string.activities_main_no_connect), Toast.LENGTH_LONG);
                        tst.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        tst.show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }
                break;
            case R.id.btnBack:
                try {
                    if (isOnline()) {
                        startActivity(intentCode);
                    } else {
                        Toast tst = Toast.makeText(MainListActivity.this, getString(R.string.activities_main_no_connect), Toast.LENGTH_LONG);
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
    public void onResponse(Call<rankingResponse> call, Response<rankingResponse> response) {
        rankingResponse rs = response.body();
        try {
            if (lastPage == 0)
                listImages.clear();

            if (rs.getRanking()!= null){
                if (rs.getRanking().size()>0){
                    listImages.addAll(rs.getRanking());
                }
            } else {
                lastPage = -1;
            }

            ipxAdapter.refresh(listImages);

        } catch (Exception e){
            e.printStackTrace();
        }
        mLoading = false;
        try {
            dialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<rankingResponse> call, Throwable t) {
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
            mLoading = true;
            if (lastPage >= 0) {
                try {
                    lastPage++;
                    Call<rankingResponse> call;
                    if (status.equals("own"))
                        call = InpixApiAdapter.getApiService().mypics(event, Integer.toString(lastPage), user);
                    else if (status.equals("rank"))
                        call = InpixApiAdapter.getApiService().ranking(event, Integer.toString(lastPage), user);
                    else
                        call = InpixApiAdapter.getApiService().lastpics(event, Integer.toString(lastPage), user);
                    call.enqueue(MainListActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + this);
                    e.printStackTrace();
                }
            }
        }
    }

}
