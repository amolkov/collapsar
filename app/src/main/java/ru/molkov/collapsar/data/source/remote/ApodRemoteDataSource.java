package ru.molkov.collapsar.data.source.remote;

import java.util.Date;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.remote.api.ApodApi;
import ru.molkov.collapsar.utils.Constants;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observable;

public class ApodRemoteDataSource extends AbstractRemoteDataSource implements IRemoteDataSource<Apod> {
    private static ApodRemoteDataSource INSTANCE;
    private static final String NASA_API_KEY = "YOUR_API_KEY";
    private static final String NASA_API_BASE_URL = "https://api.nasa.gov/planetary/";

    private final ApodApi mApodApi;

    public static ApodRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApodRemoteDataSource();
        }
        return INSTANCE;
    }

    private ApodRemoteDataSource() {
        super(NASA_API_BASE_URL);
        mApodApi = getRetrofit().create(ApodApi.class);
    }

    @Override
    public String getDateFormat() {
        return Constants.APOD_DATE_FORMAT;
    }

    @Override
    public Observable<Apod> get(Date date) {
        final String formattedDate = DateUtils.toString(date);
        return mApodApi.getApod(formattedDate, NASA_API_KEY);
    }
}
