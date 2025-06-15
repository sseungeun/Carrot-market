package com.example.carrot.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://swu-carrot.replit.app";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // null 필드도 포함하도록 설정
            Gson gson = new GsonBuilder().serializeNulls().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
