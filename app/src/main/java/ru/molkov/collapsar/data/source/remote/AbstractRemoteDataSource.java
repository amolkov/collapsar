package ru.molkov.collapsar.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.molkov.collapsar.data.core.ErrorHandlingCallAdapterFactory;

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
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    private Converter.Factory createConverterFactory() {
        Gson gson = new GsonBuilder().setDateFormat(getDateFormat()).create();
        return GsonConverterFactory.create(gson);
    }

    private CallAdapter.Factory createCallAdapterFactory() {
        return ErrorHandlingCallAdapterFactory.create();
    }
}
