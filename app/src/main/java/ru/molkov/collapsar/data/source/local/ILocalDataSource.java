package ru.molkov.collapsar.data.source.local;

import java.util.Date;

import rx.Observable;

public interface ILocalDataSource<T> {

    Observable<T> get(Date date);

    Observable<T> create(T model);

    Observable<T> update(T model);

    void delete(T model);
}
