package com.lightbox.android.inpix.activities.util;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lightbox.android.inpix.R;

import com.lightbox.android.inpix.activities.MainListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;


/**
 * Created by pablorodriguez on 18/6/18.
 */

public class ImageListAdapter extends BaseAdapter {
    private final Context ctx;
    private final List<Object> urls = new ArrayList<>();

    public ImageListAdapter(Context c, Object[] pURLS) {
        this.ctx = c;
        Collections.addAll(urls, pURLS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(ctx);
            view.setScaleType(CENTER_CROP);
        }
        // Get the image URL for the current position.
        String url = getItem(position);
        //Picasso.get().load(url).resize(100, 100).centerCrop().into(view);
        Picasso.get().load(url) //
                .placeholder(R.drawable.loader) //
                .error(R.drawable.loader) //
                .fit() //
                .centerCrop()
                .tag(this.ctx) //
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return (String)urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

}