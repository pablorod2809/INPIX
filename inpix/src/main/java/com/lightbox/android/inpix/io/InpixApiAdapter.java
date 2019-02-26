package com.lightbox.android.inpix.io;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pablorodriguez on 23/12/17.
 */

public class InpixApiAdapter {

    public static String baseUrl = "https://inpix.online/app_inpix/";
    public static String APIUrl = baseUrl + "api/public/index.php/";
    public static String ImagesUrl = baseUrl + "private/";

    private static InpixApiService API_SERVICE;

    public static InpixApiService getApiService() {

        // Creamos un interceptor y le indicamos el log level a usar
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Asociamos el interceptor a las peticiones
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //String baseUrl = "https://inpix.online/api2/public/index.php/";

        if (API_SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(InpixApiAdapter.APIUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // <-- usamos el log level
                    .build();
            API_SERVICE = retrofit.create(InpixApiService.class);
        }

        return API_SERVICE;

    }
}
