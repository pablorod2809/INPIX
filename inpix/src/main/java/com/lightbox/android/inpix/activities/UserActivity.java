package com.lightbox.android.inpix.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.common.AccountPicker;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.userResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends Activity implements OnClickListener, Callback<userResponse> {
	//Acciones
	private Button btnRegister;
	//Intents
	private Intent intentPhoto;
	//private Typeface tfDosisMedium;

	//Layout
	private int userLayout = R.layout.activity_user;
	private EditText txtName;
	private EditText txtEmail;
	private static final String TAG = "userActivity";

	private String getAcount(Context context){
    	AccountManager accountManager = AccountManager.get(context);
    	Account[] accounts = accountManager.getAccountsByType("com.google");
 	    String account;
 	    if (accounts.length > 0) {
 	      account = accounts[0].name;
		} else {
 	        account = null;
 	    }
 	    return account;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
 		setContentView(userLayout);
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );


		//intentPhoto = new Intent(UserActivity.this, CodeActivity.class);
		intentPhoto = new Intent(UserActivity.this, EventListActivity.class);

  	    // Defino las fuentes a utilizar cargandola desde el fichero .ttf        
		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);

        //Declaro objetos de la actividad principal.
       	btnRegister = (Button) findViewById(R.id.button_register);
		btnRegister.setOnClickListener(this);

        String accountCeroUser = getAcount(UserActivity.this);
		txtEmail.setText(accountCeroUser);

    }

	@Override
	public void onClick(View v) {
		// Se ejecuta al hacer click sobre algun elemento que del Activity 
		int id = v.getId();
		switch (id) {
		case R.id.button_register:
		    try {
				if (isValidEmail(txtEmail.getText().toString())) {
					this.setPrefs("opName", txtName.getText().toString());
					this.setPrefs("opEmail", txtEmail.getText().toString());
					try {
						Call<userResponse> call = InpixApiAdapter.getApiService().addUser(txtName.getText().toString(), txtEmail.getText().toString());
						call.enqueue(UserActivity.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
					startActivity(intentPhoto);
				} else {
					Toast tst = Toast.makeText(UserActivity.this, R.string.email_invalid, Toast.LENGTH_LONG);
					tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
					tst.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
	    }
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume: " + this);
		/*try {
			if (!android.provider.Settings.System.canWrite(getApplicationContext())) {
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
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

	private  static  void showSettingsPermissionDialog(final Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		final AlertDialog alert = builder.create();
		builder.setMessage("Por Favor necesito permiso para acceder a los atributos de tu camara. \n Thanks ")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
						intent.setData(Uri.parse("package:" + context.getPackageName()));
						// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
						try {
							alert.dismiss();
						} catch(Exception e){
							e.printStackTrace();
						}

					}
				});
		alert.show();
	}

	@Override
	public void onResponse(Call<userResponse> call, Response<userResponse> response) {
		userResponse rs = response.body();
		int user_id = rs.getUserId();
		this.setPrefs("opUserId", String.valueOf(user_id));
	}

	@Override
	public void onFailure(Call<userResponse> call, Throwable t) {

	}
}
