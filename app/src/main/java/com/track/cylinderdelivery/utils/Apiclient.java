package com.track.cylinderdelivery.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.track.cylinderdelivery.MySingalton;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Apiclient {
    public static final String API_URL = MySingalton.getInstance().URL +"/api/MobSalesOrder/";
    public static final String API_URL_ROCI = MySingalton.getInstance().URL +"/api/MobROCylinderMapping/";
    public static final String API_URL_CWMI = MySingalton.getInstance().URL +"/api/MobROCylinderMapping/";
    private static Retrofit retrofit = null;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getClientROCI() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL_ROCI)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getClientCWMI() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL_ROCI)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
