package com.lightbox.android.inpix.activities.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class PreferenceManager {

    public String getPrefString(String pKey, Context context) {
        SharedPreferences pref = context.getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
        String rsta = pref.getString(pKey, "NADA");
        return rsta;
    }

    public void setPrefString(String pKey,String pValue, Context context) {
        SharedPreferences pref = context.getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(pKey, pValue);
        editor.commit();
    }

    public byte[] bitmapToByteArray (Bitmap b){
        Bitmap bmp = b;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }
}
