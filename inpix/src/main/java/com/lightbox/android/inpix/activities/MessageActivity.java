package com.lightbox.android.inpix.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.lightbox.android.inpix.Util;
import com.lightbox.android.inpix.util.WSManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MessageActivity extends Activity implements OnClickListener {
	//Acciones
	private Button btnRegister;
	private Button btnCancel;
	private ImageButton btnLeft;
	private ImageButton btnRight;

	//Intents
	private Intent intentPhoto;
	private Typeface tfDosisMedium;

	//Layout
	private int msgLayout = R.layout.message_layout;
	private static final String TAG = "messageActivity";
	private ImageView preview;
	private Bitmap myPreviewImage;
	boolean frontCamera;
	private String MessageToSend;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i("debugPOV",TAG + ".onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(msgLayout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Obtengo imagen que viene por parametro.
		intentPhoto = new Intent(MessageActivity.this, Camera.class);
		Bundle bundle = getIntent().getExtras();
		int imageRotation = bundle.getInt("rotation");
		int imageOrientation = bundle.getInt("orientation");
		frontCamera = bundle.getBoolean("isfront");
		String holder = bundle.getString("holder"); //Identifica que holder seleccionó para el telefono donde se está ejecutando.
		String pathImge = bundle.getString("pathImage");
		int exifRotation = bundle.getInt("exifRotation");
		FileInputStream fis = null;

		try {
			//File location = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/images");
			File dest = new File(pathImge);
			fis = new FileInputStream(dest);
			myPreviewImage = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        MessageToSend = "Front: ";
		try {
			MessageToSend = MessageToSend + frontCamera;
			MessageToSend += " - Hold: " + holder.substring(holder.lastIndexOf('.'));
			MessageToSend += " - Rot: " + imageOrientation + "/" + imageRotation + " - ExifRot: " + exifRotation;
			MessageToSend += " - " + Build.BRAND + "-" + Build.MODEL;
		} catch (Exception e){
			e.printStackTrace();
		}
        // Acomodo al imagen
		/**
		 * Si es la camara frontal debo sumar 270º a la orientacion que viene de la camara
		 * Si es la camara trasera debo sumar 90º a la orientacion que viene de la camara
		 * Como debo expresarlo en grados de 0 a 360, sumo 270 o 90 segun corresponda y obtengo el resto de dividir por 360
		 *
		 * imageOrientation: viene con la orientacion del celular.
		 * imageRotation: viene con 270 si es front, 90 si es back
		 */
		int rotar;
		if (frontCamera) {
			rotar = ((imageOrientation + ((imageOrientation==90)||(imageOrientation==270)?180:0) + imageRotation) % 360);
			myPreviewImage = Util.rotateAndMirror(myPreviewImage, rotar, true);
		}else {
			rotar = ((imageOrientation + imageRotation) % 360);
			myPreviewImage = Util.rotate(myPreviewImage, rotar);
		}

		//Fondo del layout
		preview = (ImageView) findViewById(R.id.previewPhoto);
		preview.setImageBitmap(myPreviewImage);

        //Declaro objetos de la actividad principal.
       	btnRegister = (Button) findViewById(R.id.button_sendImage);
		btnRegister.setOnClickListener(this);

		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnCancel.setOnClickListener(this);

		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		btnLeft.setOnClickListener(this);

		btnRight = (ImageButton) findViewById(R.id.btnRight);
		btnRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// Se ejecuta al hacer click sobre algun elemento que del Activity 
		int id = v.getId();
		switch (id) {
		case R.id.button_sendImage:
			try {
				new UploadTask(this).execute(myPreviewImage);
				this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.button_cancel:
			try {
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(R.string.activities_main_sure);
				alertDialog.setMessage(R.string.activities_main_discard);
				alertDialog.setPositiveButton(R.string.activities_main_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//En caso que confirme solo debe cerrar el dialogo y luego cerrar la actividad
						finish();
					}
				});
				alertDialog.setNegativeButton(R.string.activities_main_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//En caso que confirme solo debe cerrar el dialog
					}
				});
				alertDialog.show();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.btnLeft:
			try {
				myPreviewImage = Util.rotate(myPreviewImage, 270);
				preview.setImageBitmap(myPreviewImage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			break;

		case R.id.btnRight:
		try {
			myPreviewImage = Util.rotate(myPreviewImage, 90);
			preview.setImageBitmap(myPreviewImage);
		} catch (Exception e) {
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

	private class UploadTask extends AsyncTask<Bitmap, Void, Void> {
		private Context cnt;

		UploadTask(Context pCnt){
			Log.i("debugPOV", "UploadTask.Constructor");
			cnt = pCnt;
		}

		protected Void doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null){
				return null;
			}
			setProgress(0);
			Log.i("debugPOV", "UploadTask.doInBackground");
			SharedPreferences pref = getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
			String vCode = pref.getString("opCode", "NADA");

			WSManager wsManager = new WSManager(cnt);
			wsManager.addImage(vCode,MessageToSend,0,bitmaps);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Toast tst = Toast.makeText(MessageActivity.this, R.string.activities_main_img_shared, Toast.LENGTH_LONG);
			tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
			tst.show();

		}
	}

}
