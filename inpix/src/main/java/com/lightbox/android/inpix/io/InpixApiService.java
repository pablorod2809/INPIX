package com.lightbox.android.inpix.io;

import com.lightbox.android.inpix.io.responses.addEventResponse;
import com.lightbox.android.inpix.io.responses.eventListResponse;
import com.lightbox.android.inpix.io.responses.eventResponse;
import com.lightbox.android.inpix.io.responses.imageResponse;
import com.lightbox.android.inpix.io.responses.rankingResponse;
import com.lightbox.android.inpix.io.responses.userResponse;
import com.lightbox.android.inpix.io.responses.voteResponse;
import com.lightbox.android.inpix.io.responses.messageResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by pablorodriguez on 22/12/17.
 */

public interface InpixApiService {
    @GET("validateEvent/{code}")
    Call<eventResponse> validateEvent(
            @Path("code") String codeEvent);

    @POST("addUser")
    @FormUrlEncoded
    Call<userResponse> addUser(@Field("xName") String xName,
                               @Field("xEmail") String xEmail);

    @POST("vote")
    @FormUrlEncoded
    Call<voteResponse> vote(@Field("image_id") String image_id,
                            @Field("user") String user,
                            @Field("event") String event);
    @Multipart
    @POST("addImageToEvent")
    Call<imageResponse> addImage(@Part MultipartBody.Part image,
                                 @Part("message") RequestBody message,
                                 @Part("userName") RequestBody userName,
                                 @Part("userEmail") RequestBody userEmail,
                                 @Part("event") RequestBody event);

    @GET("ranking/{event}/{page}/{user}")
    Call<rankingResponse> ranking(
            @Path("event") String codeEvent,
            @Path("page") String page,
            @Path("user") String user);

    @GET("mypics/{event}/{page}/{user}")
    Call<rankingResponse> mypics(
            @Path("event") String codeEvent,
            @Path("page") String page,
            @Path("user") String user);

    @GET("lastpics/{event}/{page}/{user}")
    Call<rankingResponse> lastpics(
            @Path("event") String codeEvent,
            @Path("page") String page,
            @Path("user") String user);

    @GET("messages/{event}/{lan}")
    Call<messageResponse> messages(
            @Path("event") String codeEvent,
            @Path("lan") String lang);

    @GET("listevents/{page}/{lan}")
    Call<eventListResponse> listEvents(
            @Path("page") String page,
            @Path("lan") String lang);

    @Multipart
    @POST("addEvent")
    Call<addEventResponse> addEvent(@Part MultipartBody.Part image,
                                    @Part("eventName") RequestBody eventName,
                                    @Part("eventTitle") RequestBody eventTitle,
                                    @Part("eventDescription") RequestBody eventDescription,
                                    @Part("eventLanguage") RequestBody eventLanguage,
                                    @Part("eventVisibility") RequestBody eventVisibility,
                                    @Part("userEmail") RequestBody userEmail,
                                    @Part("userPassword") RequestBody userPassword);

}

