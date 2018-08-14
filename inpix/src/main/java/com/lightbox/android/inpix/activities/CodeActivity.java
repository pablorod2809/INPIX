package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.eventResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeActivity extends Activity implements OnClickListener, Callback<eventResponse> {
	//Acciones
	private Button btnRegisterCode;

	//Intents
	private Intent intentPhoto;

	//Layout
	private int codeLayout = R.layout.activity_code;
	private EditText txtCode;
	private static final String TAG = "codeActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
 		setContentView(codeLayout);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );

		try {
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
		} catch (Exception e) {
			e.printStackTrace();

		}

		//intentPhoto = new Intent(CodeActivity.this, MainActivity.class);
		intentPhoto = new Intent(CodeActivity.this, MainListActivity.class);

  	    // Defino las fuentes a utilizar cargandola desde el fichero .ttf        
		txtCode = (EditText) findViewById(R.id.txtCode);

        //Declaro objetos de la actividad principal.
       	btnRegisterCode = (Button) findViewById(R.id.button_registercode);
		btnRegisterCode.setOnClickListener(this);

		//Muestro el nombre del usuario logueado.
		((TextView) findViewById(R.id.lblUserName)).setText(getString(R.string.hello).concat(" ") + getPrefs("opName"));
    }

	@Override
	public void onClick(View v) {
		// Se ejecuta al hacer click sobre algun elemento que del Activity 
		int id = v.getId();
		switch (id) {
		case R.id.button_registercode:
		    try {
				Call<eventResponse> call = InpixApiAdapter.getApiService().validateEvent(txtCode.getText().toString().toUpperCase());
				call.enqueue(CodeActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
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

	//REspuesta cuando se ejecuta la validacion de evento.
	@Override
	public void onResponse(Call<eventResponse> call, Response<eventResponse> response) {
		if(response.isSuccessful()){
			this.setPrefs("opEvtId",response.body().getId());
			this.setPrefs("opCode",txtCode.getText().toString().toUpperCase());
			this.setPrefs("opEvtDesc", response.body().getName());
			this.setPrefs("opEvtFrom",response.body().getFrom());
			this.setPrefs("opEvtTo",response.body().getTo());
			startActivity(intentPhoto);
		} else {
			this.setPrefs("opCode","0");
			this.setPrefs("opEvtId", "0");
			this.setPrefs("opEvtDesc", "NADA");
			this.setPrefs("opEvtFrom","");
			this.setPrefs("opEvtTo","");
			Toast tst = Toast.makeText(CodeActivity.this, "Este código es invalido"+'\n'+"por favor vuleva a intentarlo", Toast.LENGTH_LONG);
			tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
			tst.show();
			this.txtCode.setText(null);
		}

	}

	@Override
	public void onFailure(Call<eventResponse> call, Throwable t) {

	}
}
