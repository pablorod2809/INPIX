package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
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
import com.lightbox.android.inpix.util.WSManager;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class AboutActivity extends Activity implements OnClickListener {
	//Layout
	private int aboutLayout = R.layout.activity_about;
	private static final String TAG = "aboutActivity";

	//Varios
	private TextView lblBrand;
	private TextView lblModel;
	private TextView lblDriver;
	private TextView lblVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
 		setContentView(aboutLayout);

		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = pInfo.versionName;

		lblVersionName = (TextView) findViewById(R.id.lblVersionName);
		lblBrand = (TextView) findViewById(R.id.lblBrand);
		lblModel = (TextView) findViewById(R.id.lblModel);
		lblDriver = (TextView) findViewById(R.id.lblDriver);

		//Muestro el nombre del usuario logueado.
		lblBrand.setText("Mark: " + Build.BRAND);
		lblModel.setText("Modelo: " + Build.MODEL);
		lblDriver.setText("Serial: " + Build.SERIAL);
		lblVersionName.setText(getString(R.string.activities_about_version)+" "+version);

    }

	@Override
	public void onClick(View v) {
		// Se ejecuta al hacer click sobre algun elemento que del Activity 
		int id = v.getId();
	}

	@Override
	protected void onResume() {
		super.onResume();
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

}
