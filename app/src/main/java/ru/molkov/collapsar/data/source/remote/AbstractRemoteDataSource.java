package ru.molkov.collapsar.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.CallAdapter;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public abstract class AbstractRemoteDataSource {
    private final Retrofit mRetrofit;

    public AbstractRemoteDataSource(String apiBaseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(createConverterFactory())
                .addCallAdapterFactory(createCallAdapterFactory())
                .build();
    }

    public abstract String getDateFormat();

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient();
    }

    private Converter.Factory createConverterFactory() {
        Gson gson = new GsonBuilder().setDateFormat(getDateFormat()).create();
        return GsonConverterFactory.create(gson);
    }

    private CallAdapter.Factory createCallAdapterFactory() {
        return RxJavaCallAdapterFactory.create();
    }
}
