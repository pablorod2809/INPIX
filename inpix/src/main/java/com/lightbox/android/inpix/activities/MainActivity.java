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

public class MainActivity extends Activity implements OnClickListener {
	//Acciones
	private Button btnTakePhoto;
	private Button btnChangeEvt;
	private ImageButton btnMenu;

	//Intents
	private Intent intentPhoto;
	private Intent intentCode;
	private Intent intentAbout;

	//Layouts
	private int mainLayout = R.layout.activity_main;
	private TextView lblMessage, lblWelcome, lblYourPictures;
	private static final String TAG = "INPIX-mainActivity";

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

		intentPhoto = new Intent(MainActivity.this, Camera.class);
        intentCode = new Intent(MainActivity.this, EventActivity.class);
		intentAbout = new Intent(MainActivity.this, AboutActivity.class);

        //Declaro objetos de la actividad principal.
       	btnTakePhoto = (Button) findViewById(R.id.button_takephoto);
		btnTakePhoto.setOnClickListener(this);

		btnChangeEvt = (Button) findViewById(R.id.button_changeevt);
		btnChangeEvt.setOnClickListener(this);

		btnMenu = (ImageButton) findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);

		lblMessage = (TextView) findViewById(R.id.message);
		lblWelcome =  (TextView) findViewById(R.id.lbl_welcome);
        lblYourPictures = (TextView) findViewById(R.id.yourPictures);

		String message = getString(R.string.hello) + " " + getPrefs("opName") + "!";
		lblMessage.setText(getPrefs("opEvtDesc"));
		lblWelcome.setText(message);
        lblYourPictures.setText(getString(R.string.your_pics) + " https://inpix.online/wall?e=" + getPrefs("opCode"));
        lblYourPictures.setOnClickListener(this);

	}

    private boolean isOnline() {
    	String netName = null;
    	//String ipAddress = null;
    	
        try{
            WifiManager cm;
			cm = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			//TODO: verificar que el sistema este conectado a internet cm.getWifiState();
    	    netName = cm.getConnectionInfo().getSSID();
        }catch(Exception e){
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
		case R.id.button_takephoto:
		    try {
			  if (isOnline()){
				  startActivity(intentPhoto);
			  }else{
				  Toast tst = Toast.makeText(MainActivity.this, "No estas conectado a internet"+'\n' + " necesitas conectarte para poder subir fotos", Toast.LENGTH_LONG);
				  tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
				  tst.show();
			   }
			} catch (Exception e) {
				Log.e(TAG, "onClick: " + this);
				e.printStackTrace();
			}
			break;

		case R.id.btnMenu:
			try {
					startActivity(intentAbout);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

		case R.id.button_changeevt:
		    try {
		    	startActivity(intentCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    break;
        case R.id.yourPictures:
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://inpix.online/wall?e=" + getPrefs("opCode")));
                startActivity(browserIntent);
            } catch (Exception e){
                e.printStackTrace();
            }
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
