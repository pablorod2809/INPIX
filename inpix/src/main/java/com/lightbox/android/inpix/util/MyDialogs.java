package com.lightbox.android.inpix.util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by pablorodriguez on 1/10/15.
 */
public class MyDialogs extends AlertDialog {

    Context mContext;

    public MyDialogs(Context context) {
        super(context);
        this.mContext = context;
    }

}
