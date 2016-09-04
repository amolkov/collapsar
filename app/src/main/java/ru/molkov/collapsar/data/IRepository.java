package ru.molkov.collapsar.data;

import java.util.Date;
import java.util.List;

import rx.Observable;

public interface IRepository<T> {

    Observable<T> get(Date date);

    Observable<List<T>> getList(List<Date> dates);

    Observable<T> create(T model);

    Observable<T> update(T model);

    void delete(T model);
}
