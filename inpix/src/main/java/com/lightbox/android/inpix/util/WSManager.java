package com.lightbox.android.inpix.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.imageResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pablorodriguez on 14/9/15.
 */

public class WSManager implements Callback<imageResponse> {

    private static final String TAG = "WSManager";
    private Bitmap imageToSend;
    private Context context;
    private SharedPreferences pref;

    public WSManager(Context pContext){
        context = pContext;
        pref = context.getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
    }


    public void addImage(String pCode, String pMessage, int pRotation, Bitmap... pBitmap){
        if (pBitmap[0] == null){
            Log.i(TAG, "addImage.Return por null ");
            return;
        } else {
            imageToSend = pBitmap[0];
        }

        //Verifico si la imagen está apaisada, en ese caso se voltea.
        if (imageToSend.getWidth() > imageToSend.getHeight() && pRotation>0){
            Matrix matrix = new Matrix();
            matrix.postRotate(pRotation);
            imageToSend = Bitmap.createBitmap(imageToSend, 0, 0,
                    imageToSend.getWidth(), imageToSend.getHeight(), matrix, true);
        }

        //Comprimir la imagen
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageToSend.compress(Bitmap.CompressFormat.PNG, 32, stream);
        InputStream in = new ByteArrayInputStream(stream.toByteArray());

        //Obtener datos del usuario y procesar nombre de imagen.
        String myUserName  = pref.getString("opName", "Sin Nombre");
        String myUserEmail = pref.getString("opEmail", "Sin Email");
        String fileName    = "IMG_" + System.currentTimeMillis() + ".png";

        File file = MyFiles.inputStreamToFile(in, fileName, this.context);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), pMessage);
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), myUserName);
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), myUserEmail);
        RequestBody event = RequestBody.create(MediaType.parse("text/plain"), pCode);

        Call<imageResponse> call = InpixApiAdapter.getApiService().addImage(body,message,userName,userEmail,event);
        call.enqueue(WSManager.this);

    }

    @Override
    public void onResponse(Call<imageResponse> call, Response<imageResponse> response) {

    }

    @Override
    public void onFailure(Call<imageResponse> call, Throwable t) {

    }

}
