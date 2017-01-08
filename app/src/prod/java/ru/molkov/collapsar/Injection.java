package ru.molkov.collapsar;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.molkov.collapsar.data.ApodRepository;
import ru.molkov.collapsar.data.source.local.ApodLocalDataSource;
import ru.molkov.collapsar.data.source.remote.ApodRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static ApodRepository provideApodRepository(@NonNull Context context) {
        checkNotNull(context);
        return ApodRepository.getInstance(ApodLocalDataSource.getInstance(context),
                ApodRemoteDataSource.getInstance());
    }

}
