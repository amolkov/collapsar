package ru.molkov.collapsar;

import android.content.Context;

import ru.molkov.collapsar.data.ApodRepository;
import ru.molkov.collapsar.data.source.local.ApodLocalDataSource;
import ru.molkov.collapsar.data.source.remote.ApodRemoteDataSource;

public class Injection {

    public static ApodRepository provideApodRepository(Context context) {
        return ApodRepository.getInstance(ApodRemoteDataSource.getInstance(),
                ApodLocalDataSource.getInstance(context));
    }

}
