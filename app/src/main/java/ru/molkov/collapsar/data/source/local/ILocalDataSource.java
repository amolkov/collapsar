package ru.molkov.collapsar.data.source.local;

import android.support.annotation.NonNull;

import java.util.Date;

import rx.Observable;

public interface ILocalDataSource<T> {

    Observable<T> get(@NonNull Date date);

    Observable<T> create(@NonNull T model);

    Observable<T> update(@NonNull T model);

    void delete(@NonNull T model);

    void deleteAll();
}
