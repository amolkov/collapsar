package ru.molkov.collapsar.data.source.remote;

import android.support.annotation.NonNull;

import java.util.Date;

import rx.Observable;

public interface IRemoteDataSource<T> {

    Observable<T> get(@NonNull Date date);
}
