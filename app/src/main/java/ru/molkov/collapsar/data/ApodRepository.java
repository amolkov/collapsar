package ru.molkov.collapsar.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.molkov.collapsar.App;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.local.ILocalDataSource;
import ru.molkov.collapsar.data.source.remote.IRemoteDataSource;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApodRepository implements IRepository<Apod> {
    private static ApodRepository INSTANCE = null;
    private final ILocalDataSource<Apod> mLocalDataSource;
    private final IRemoteDataSource<Apod> mRemoteDataSource;

    private Map<String, Apod> mCachedData;

    private ApodRepository(@NonNull ILocalDataSource<Apod> localDataSource,
                           @NonNull IRemoteDataSource<Apod> remoteDataSource) {
        mLocalDataSource = checkNotNull(localDataSource);
        mRemoteDataSource = checkNotNull(remoteDataSource);
        mCachedData = new HashMap<>();
    }

    public static ApodRepository getInstance(ILocalDataSource<Apod> localDataSource,
                                             IRemoteDataSource<Apod> remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ApodRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public Observable<Apod> get(Date date) {
        /*
        final Apod cachedApod = getFromCache(date);
        if (cachedApod != null) {
            return Observable.just(cachedApod);
        }
        */
        Observable<Apod> localApod = mLocalDataSource.get(date).doOnNext(apod -> putToCache(apod)).first();

        if (!App.isNetworkAvailable()) {
            return localApod.filter(apod -> apod != null);
        } else {
            Observable<Apod> remoteApod = mRemoteDataSource.get(date).doOnNext(apod -> create(apod));
            return Observable
                    .concat(localApod, remoteApod)
                    .takeFirst(apod -> apod != null);
        }
    }


    @Override
    public Observable<List<Apod>> getList(List<Date> dates) {
        return Observable
                .merge(getObservables(dates))
                .filter(apod -> isSupported(apod))
                .toSortedList((first, second) -> first.getDate().before(second.getDate()) ? 1 : -1);
    }

    @Override
    public Observable<Apod> create(Apod model) {
        if (model != null) {
            return mLocalDataSource.create(model).doOnNext(apod -> putToCache(apod));
        }
        return Observable.empty();
    }

    @Override
    public Observable<Apod> update(Apod model) {
        return mLocalDataSource.update(model);
    }

    @Override
    public void delete(Apod model) {
        mLocalDataSource.delete(model);
    }

    private Apod getFromCache(Date date) {
        if (mCachedData.isEmpty()) {
            return null;
        }
        return mCachedData.get(DateUtils.toString(date));
    }

    private void putToCache(Apod apod) {
        if (apod != null) {
            mCachedData.put(DateUtils.toString(apod.getDate()), apod);
        }
    }

    private boolean isSupported(Apod apod) {
        if (apod.getMediaType().equalsIgnoreCase("image")) {
            return true;
        }
        return ImageUtils.isSupportedVideo(apod.getUrl());
    }

    private List<Observable<Apod>> getObservables(List<Date> dates) {
        List<Observable<Apod>> observables = new ArrayList<>();
        for (Date date : dates) {
            observables.add(ApodRepository.this.get(date));
        }
        return observables;
    }
}
