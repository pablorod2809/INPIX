package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.eventResponse;
import com.lightbox.android.inpix.util.WSManager;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends Activity implements OnClickListener, Callback<eventResponse> {
	//Acciones
	private Button btnRegisterCode;

	//Intents
	private Intent intentPresentation;
    private Intent intentCode;
    private Intent intentMainList;
	//private Typeface tfDosisMedium;
    private String codeEvent;

	//Layout
	private int evetLayout = R.layout.activity_events;
	private static final String TAG = "eventActivity";
    private ImageButton indiaBtn;
	private ImageButton naturaBtn;
	private ImageButton russiaBtn;
	private ImageView smileBtn;
    private ImageView loveBtn;
    private ImageButton lockBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
 		setContentView(evetLayout);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );

		intentPresentation = new Intent(EventActivity.this, PresentationActivity.class);
        intentCode = new Intent(EventActivity.this, CodeActivity.class);
        intentMainList = new Intent(EventActivity.this, MainListActivity.class);

  	    // Defino las fuentes a utilizar cargandola desde el fichero .ttf        
        loveBtn = (ImageView) findViewById(R.id.lovebtn);
		indiaBtn = (ImageButton) findViewById(R.id.indiaBtn);
		naturaBtn = (ImageButton) findViewById(R.id.naturaBtn);
		russiaBtn = (ImageButton) findViewById(R.id.russiaBtn);
		smileBtn = (ImageView) findViewById(R.id.smileBtn);
        lockBtn = (ImageButton) findViewById(R.id.lockBtn);

        //Declaro objetos de la actividad principal.
        loveBtn.setOnClickListener(this);
		indiaBtn.setOnClickListener(this);
		naturaBtn.setOnClickListener(this);
		russiaBtn.setOnClickListener(this);
		smileBtn.setOnClickListener(this);
        lockBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// Se ejecuta al hacer click sobre algun elemento que del Activity
		int id = v.getId();
        codeEvent = "LOCK";
		switch (id) {
            case R.id.lovebtn:
                codeEvent = "LOVE";
                break;
			case R.id.russiaBtn:
				codeEvent = "PEACE";
				break;
			case R.id.naturaBtn:
				codeEvent = "NATURA";
				break;
			case R.id.indiaBtn:
				codeEvent = "INDIA";
				break;
			case R.id.smileBtn:
				codeEvent = "SMILE";
				break;
            case R.id.lockBtn:
            	codeEvent = "LOCK";
                break;
	    }

        if(codeEvent.equals("LOCK")) {
            startActivity(intentCode);
            //startActivity(intentMainList);
        } else {

            try{
				Call<eventResponse> call = InpixApiAdapter.getApiService().validateEvent(codeEvent);
				call.enqueue(EventActivity.this);
				this.setPrefs("opCode",codeEvent);
				this.setPrefs("opEvtCode",codeEvent);
            } catch (Exception e){
                this.setPrefs("opCode","0");
                this.setPrefs("opEvtId", "0");
                this.setPrefs("opEvtDesc", "NADA");
                this.setPrefs("opEvtFrom","");
                this.setPrefs("opEvtTo","");
                Toast tst = Toast.makeText(EventActivity.this, "Este código es invalido"+'\n'+"por favor vuleva a intentarlo", Toast.LENGTH_LONG);
                tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
                tst.show();
            }
        }

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
			Log.i(TAG, "Acelerometer OK !!!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "onResume: " + this);
    }

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onResume: " + this);
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

	public void setPrefs(String pKey,String pValue) {
		SharedPreferences pref = getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(pKey, pValue);
		editor.commit();
	}


	public String getPrefs(String pKey) {
		SharedPreferences pref = getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
		String rsta = pref.getString(pKey, "NADA");
		return rsta;
	}

	@Override
	public void onResponse(Call<eventResponse> call, Response<eventResponse> response) {
		if(response.isSuccessful()){
			this.setPrefs("opEvtId", response.body().getId());
			this.setPrefs("opEvtDesc", response.body().getName());
			this.setPrefs("opEvtFrom",response.body().getFrom());
			this.setPrefs("opEvtTo",response.body().getTo());
			//startActivity(intentMain);
            if (this.getPrefs(codeEvent).equals("NADA")){
                this.setPrefs(codeEvent,"ok");
                startActivity(intentPresentation);
            }else {
                startActivity(intentMainList);
            }
		} else {
			this.setPrefs("opCode","0");
			this.setPrefs("opEvtId", "0");
			this.setPrefs("opEvtDesc", "NADA");
			this.setPrefs("opEvtFrom","");
			this.setPrefs("opEvtTo","");
			Toast tst = Toast.makeText(EventActivity.this, "Este código es invalido"+'\n'+"por favor vuleva a intentarlo", Toast.LENGTH_LONG);
			tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
			tst.show();
		}
	}
	@Override
	public void onFailure(Call<eventResponse> call, Throwable t) {

	}
}
