package ru.molkov.collapsar.data;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.local.ILocalDataSource;
import ru.molkov.collapsar.data.source.remote.IRemoteDataSource;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class ApodRepository implements IRepository<Apod> {
    private static ApodRepository INSTANCE = null;
    private final IRemoteDataSource<Apod> mRemoteDataSource;
    private final ILocalDataSource<Apod> mLocalDataSource;

    private Map<String, Apod> mCachedData;

    public static ApodRepository getInstance(IRemoteDataSource<Apod> remoteDataSource, ILocalDataSource<Apod> localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ApodRepository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    private ApodRepository(IRemoteDataSource<Apod> remoteDataSource, ILocalDataSource<Apod> localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
        mCachedData = new HashMap<>();
    }

    @Override
    public Observable<Apod> get(Date date) {
        final Apod cachedApod = getFromCache(date);
        if (cachedApod != null) {
            return Observable.just(cachedApod);
        }

        Observable<Apod> localApod = mLocalDataSource.get(date).doOnNext(new Action1<Apod>() {
            @Override
            public void call(Apod apod) {
                ApodRepository.this.putToCache(apod);
            }
        });
        Observable<Apod> remoteApod = mRemoteDataSource.get(date).doOnNext(new Action1<Apod>() {
            @Override
            public void call(Apod apod) {
                ApodRepository.this.create(apod);
            }
        });
        return Observable.concat(localApod.first(), remoteApod).filter(new Func1<Apod, Boolean>() {
            @Override
            public Boolean call(Apod apod) {
                return apod != null;
            }
        }).first();
    }


    @Override
    public Observable<List<Apod>> getList(List<Date> dates) {
        return Observable
                .merge(getObservables(dates))
                .toSortedList(new Func2<Apod, Apod, Integer>() {
                    @Override
                    public Integer call(Apod first, Apod second) {
                        return first.getDate().before(second.getDate()) ? 1 : -1;
                    }
                });
    }

    @Override
    public Observable<Apod> create(Apod model) {
        return mLocalDataSource.create(model).doOnNext(new Action1<Apod>() {
            @Override
            public void call(Apod apod) {
                ApodRepository.this.putToCache(apod);
            }
        });
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

    private List<Observable<Apod>> getObservables(List<Date> dates) {
        List<Observable<Apod>> observables = new ArrayList<>();
        for (Date date : dates) {
            observables.add(ApodRepository.this.get(date));
        }
        return observables;
    }
}
