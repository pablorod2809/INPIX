package com.lightbox.android.inpix.activities.util;

/**
 * Created by pablorodriguez on 20/6/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.inpix.CameraApplication;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.CodeActivity;
import com.lightbox.android.inpix.activities.EventListActivity;
import com.lightbox.android.inpix.activities.MainDetailActivity;
import com.lightbox.android.inpix.activities.MainListActivity;
import com.lightbox.android.inpix.activities.PresentationActivity;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.eventResponse;
import com.lightbox.android.inpix.io.responses.rankingResponse;
import com.lightbox.android.inpix.io.responses.voteResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InpixEventAdapter extends BaseAdapter implements Callback<voteResponse> {
    private final static String TAG = "InpixImageAdapter";
    private Context ctx;
    private String path;
    private String lang;
    private ArrayList<eventResponse> events;

    public InpixEventAdapter(Context c, ArrayList<eventResponse> pEvents, String lang, String path){   //, DetailsAdapterListener listener) {
        this.ctx = c;
        this.events = pEvents;
        this.path = path;
        this.lang = lang;
    }

    public void refresh(ArrayList<eventResponse> pEvents){
        this.events = pEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderEvent holder;
        final eventResponse s;
        s = (eventResponse) getItem(i);
        if(view==null) {
            view= LayoutInflater.from(ctx).inflate(R.layout.item_event,viewGroup,false);
            holder = new ViewHolderEvent(view,s);
            view.setTag(holder);
        } else {
            holder = (ViewHolderEvent) view.getTag();
        }

        try {
            String urlBg = path + s.getCode() + s.getBackground();
            String urlLogo = path + s.getCode() + s.getLogo();
            Log.i(TAG, urlBg);
            //Inicializo la tarjeta
            CameraApplication app = ((EventListActivity)ctx).getApp();
            app.setTypeface(holder.title);
            app.setTypeface(holder.votes);
            app.setTypeface(holder.subtitle);
            holder.urlBg = urlBg;
            holder.urlLogo = urlLogo;
            holder.title.setText(s.getTitle());
            holder.subtitle.setText(s.getSubtitle());
            holder.votes.setText(s.getImgs());
            holder.currentEvent = s;


            //Cargo imagen
            Picasso.get().load(urlBg)
                         .fit()
                         .centerCrop()
                         .tag(this.ctx)
                         .into(holder.bgimage);

            Picasso.get().load(urlLogo)
                    .fit()
                    .centerCrop()
                    .tag(this.ctx)
                    .into(holder.logo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResponse(Call<voteResponse> call, Response<voteResponse> response) {
        voteResponse v = response.body();
        Log.i(TAG,"respuesta" + v.getVote());
    }

    @Override
    public void onFailure(Call<voteResponse> call, Throwable t) {

    }

    public class ViewHolderEvent implements Callback<eventResponse> {
        String urlBg;
        String urlLogo;

        TextView title;
        TextView subtitle;
        TextView votes;

        ImageView bgimage;
        ImageView logo;

        eventResponse currentEvent;

        public ViewHolderEvent (View itemView, final eventResponse evtValue){
            bgimage = itemView.findViewById(R.id.bgImage);
            votes = itemView.findViewById(R.id.votes);
            title = itemView.findViewById(R.id.titleItemEvent);
            subtitle = itemView.findViewById(R.id.subtitleItemEvent);
            logo = itemView.findViewById(R.id.logoEvent);
            currentEvent = evtValue;
            if (currentEvent.getType().equals("PRI"))
                itemView.findViewById(R.id.imgPrivate).setVisibility(View.VISIBLE);

            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Tipo:--->" + currentEvent.getType());
                    Log.i(TAG,"Codigo:--->" + currentEvent.getCode());
                    callEvent(currentEvent.getCode(),currentEvent.getType());
                    setPrefs("url",urlBg);
                }
            });
        }

        public void callEvent(String event, String type) {
            if (type.equals("PRI")) {
                Intent intentCodeActivity = new Intent(ctx, CodeActivity.class);
                this.setPrefs("opCode", "0");
                this.setPrefs("opEvtId", "0");
                ctx.startActivity(intentCodeActivity);
            } else {
                try {
                    Call<eventResponse> call = InpixApiAdapter.getApiService().validateEvent(event);
                    call.enqueue(this);
                    this.setPrefs("opCode", event);
                    this.setPrefs("opEvtCode", event);
                } catch (Exception e) {
                    this.setPrefs("opCode", "0");
                    this.setPrefs("opEvtId", "0");
                    this.setPrefs("opEvtDesc", "NADA");
                    this.setPrefs("opEvtFrom", "");
                    this.setPrefs("opEvtTo", "");
                    Toast tst = Toast.makeText(ctx, "Este código es invalido" + '\n' + "por favor vuleva a intentarlo", Toast.LENGTH_LONG);
                    tst.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    tst.show();
                }
            }
        }


        public void setPrefs(String pKey,String pValue) {
            SharedPreferences pref = ctx.getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(pKey, pValue);
            editor.commit();
        }

        public String getPrefs(String pKey) {
            SharedPreferences pref = ctx.getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
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
                if (this.getPrefs(response.body().getCode()).equals("NADA")){
                    this.setPrefs(response.body().getCode(),"ok");
                    Intent intentPresActivity = new Intent(ctx, PresentationActivity.class);
                    ctx.startActivity(intentPresActivity);
                }else {
                    Intent intentMainActivity = new Intent(ctx, MainListActivity.class);
                    ctx.startActivity(intentMainActivity);
                }
            } else {
                this.setPrefs("opCode","0");
                this.setPrefs("opEvtId", "0");
                this.setPrefs("opEvtDesc", "NADA");
                this.setPrefs("opEvtFrom","");
                this.setPrefs("opEvtTo","");
                Toast tst = Toast.makeText(ctx, "Este código es invalido"+'\n'+"por favor vuleva a intentarlo", Toast.LENGTH_LONG);
                tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
                tst.show();
            }

        }

        @Override
        public void onFailure(Call<eventResponse> call, Throwable t) {

        }
    }

}