package com.lightbox.android.inpix.activities.util;

/**
 * Created by pablorodriguez on 20/6/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lightbox.android.inpix.CameraApplication;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.MainDetailActivity;
import com.lightbox.android.inpix.activities.MainListActivity;
import com.lightbox.android.inpix.io.InpixApiAdapter;
import com.lightbox.android.inpix.io.responses.rankingResponse;
import com.lightbox.android.inpix.io.responses.voteResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InpixImageAdapter extends BaseAdapter implements Callback<voteResponse> {
    private final static String TAG = "InpixImageAdapter";
    private Context ctx;
    private String path;
    private ArrayList<rankingResponse.imageRankValue> rankImages;

    public InpixImageAdapter (Context c, ArrayList<rankingResponse.imageRankValue> pRankImages,String path){   //, DetailsAdapterListener listener) {
        this.ctx = c;
        this.rankImages = pRankImages;
        this.path = path;
    }

    public void refresh(ArrayList<rankingResponse.imageRankValue> pRankImages){
        this.rankImages = pRankImages;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rankImages.size();
    }

    @Override
    public Object getItem(int i) {
        return rankImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        final rankingResponse.imageRankValue s;
        s = (rankingResponse.imageRankValue)getItem(i);
        if(view==null) {
            view= LayoutInflater.from(ctx).inflate(R.layout.item_image,viewGroup,false);
            holder = new ViewHolder(view,s);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            String url = path + s.getImage();
            //Inicializo la tarjeta
            CameraApplication app = ((MainListActivity)ctx).getApp();
            app.setTypeface(holder.name);
            app.setTypeface(holder.votes);
            holder.url = url;
            holder.name.setText(s.getUser());
            holder.votes.setText(String.valueOf(s.getVotes()));
            holder.rnk = s;
            if (s.getMyVote() == 0)
                holder.cuore.setImageResource(R.drawable.heart_grey);
            else
                holder.cuore.setImageResource(R.drawable.heart_red);

            //Cargo imagen
            Picasso.get().load(url)
                         .fit()
                         .centerCrop()
                         .tag(this.ctx)
                         .into(holder.image);

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

    public class ViewHolder implements Callback<voteResponse> {
        ImageView image;
        String url;
        TextView name;
        TextView votes;
        ImageView cuore;
        ImageView fullImage;

        rankingResponse.imageRankValue rnk;
        private String event;
        private String userId;

        public ViewHolder(View itemView, rankingResponse.imageRankValue rankValue){
            image = itemView.findViewById(R.id.bgImage);
            votes = itemView.findViewById(R.id.votesTxt);
            name = itemView.findViewById(R.id.votes);
            cuore = itemView.findViewById(R.id.imgCoure);
            fullImage = itemView.findViewById(R.id.fullImage);
            rnk = rankValue;

            SharedPreferences pref = ctx.getSharedPreferences("mypreferences", Context.MODE_PRIVATE);
            event = pref.getString("opCode","NULL");
            userId = pref.getString("opUserId",pref.getString("opEmail","NULL"));

            cuore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Foto VOTO: " + rnk.getId() + " + evt: " + event + " + user: " +  rnk.getUser() + " + votos: " + rnk.getVotes() + " + yo: " + userId);
                    if (rnk.getMyVote() == 0) {
                        cuore.setImageResource(R.drawable.heart_red);
                    } else {
                        cuore.setImageResource(R.drawable.heart_grey);
                    }
                    Call<voteResponse> call_vote = InpixApiAdapter.getApiService().vote(String.valueOf(rnk.getId()),userId,event);
                    call_vote.enqueue(ViewHolder.this);
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Foto IMG: " + rnk.getId() + " + evt: " + event + " + user: " +  rnk.getUser() + " + votos: " + rnk.getVotes() + " + yo: " + userId);
                    setPrefs("url",url);
                    Intent intentDetailActivity = new Intent(ctx, MainDetailActivity.class);
                    ctx.startActivity(intentDetailActivity);
                }
            });
        }

        //Respuesta de voto.
        @Override
        public void onResponse(Call<voteResponse> call, Response<voteResponse> response) {

            rnk.setMyVote();
        }

        //Respuesta del voto con error
        @Override
        public void onFailure(Call<voteResponse> call, Throwable t) {

        }

        public void setPrefs(String pKey,String pValue) {
            SharedPreferences pref = ctx.getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(pKey, pValue);
            editor.commit();
        }
    }

}