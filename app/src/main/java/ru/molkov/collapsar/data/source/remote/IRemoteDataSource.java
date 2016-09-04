package ru.molkov.collapsar.data.source.remote;

import java.util.Date;

import rx.Observable;

public interface IRemoteDataSource<T> {

    Observable<T> get(Date date);
}
