package ru.molkov.collapsar.data.source.remote.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.molkov.collapsar.data.model.Apod;
import rx.Observable;

public interface ApodApi {

    @GET("apod")
    Observable<Apod> getApod(@Query("date") String date,
                             @Query("api_key") String apiKey
    );
}
