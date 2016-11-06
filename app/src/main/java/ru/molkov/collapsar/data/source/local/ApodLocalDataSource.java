package ru.molkov.collapsar.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Date;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.local.ApodPersistenceContract.ApodEntry;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ApodLocalDataSource implements ILocalDataSource<Apod> {
    private static ApodLocalDataSource INSTANCE;
    private final BriteDatabase mDatabaseHelper;
    private Func1<Cursor, Apod> mMapperFunction;

    public static ApodLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ApodLocalDataSource(context);
        }
        return INSTANCE;
    }

    private ApodLocalDataSource(Context context) {
        if (context == null) {
            throw new NullPointerException();
        }

        ApodDbHelper dbHelper = new ApodDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        mMapperFunction = c -> {
            String copyright = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_COPYRIGHT));
            String date = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_DATE));
            Date parsedDate = DateUtils.toDate(date);
            String explanation = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_EXPLANATION));
            String mediaType = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_MEDIA_TYPE));
            String title = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_TITLE));
            String url = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_URL));
            String urlHd = c.getString(c.getColumnIndexOrThrow(ApodEntry.COLUMN_NAME_URL_HD));
            return new Apod(copyright, parsedDate, explanation, mediaType, title, url, urlHd);
        };
    }

    @Override
    public Observable<Apod> get(Date date) {
        String[] projection = {
                ApodEntry.COLUMN_NAME_COPYRIGHT,
                ApodEntry.COLUMN_NAME_DATE,
                ApodEntry.COLUMN_NAME_EXPLANATION,
                ApodEntry.COLUMN_NAME_MEDIA_TYPE,
                ApodEntry.COLUMN_NAME_TITLE,
                ApodEntry.COLUMN_NAME_URL,
                ApodEntry.COLUMN_NAME_URL_HD
        };

        String sql = String.format("SELECT %s FROM %s WHERE %s=?",
                TextUtils.join(",", projection), ApodEntry.TABLE_NAME, ApodEntry.COLUMN_NAME_DATE);
        return mDatabaseHelper.createQuery(ApodEntry.TABLE_NAME, sql, DateUtils.toString(date))
                .mapToOneOrDefault(mMapperFunction, null);
    }

    @Override
    public Observable<Apod> create(Apod model) {
        if (model == null) {
            return Observable.just(null);
        }

        ContentValues values = new ContentValues();
        values.put(ApodEntry.COLUMN_NAME_COPYRIGHT, model.getCopyright());
        values.put(ApodEntry.COLUMN_NAME_DATE, DateUtils.toString(model.getDate()));
        values.put(ApodEntry.COLUMN_NAME_EXPLANATION, model.getExplanation());
        values.put(ApodEntry.COLUMN_NAME_MEDIA_TYPE, model.getMediaType());
        values.put(ApodEntry.COLUMN_NAME_TITLE, model.getTitle());
        values.put(ApodEntry.COLUMN_NAME_URL, model.getUrl());
        values.put(ApodEntry.COLUMN_NAME_URL_HD, model.getUrlHd());
        mDatabaseHelper.insert(ApodEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);

        return get(model.getDate());
    }

    @Override
    public Observable<Apod> update(Apod model) {
        //TODO
        return null;
    }

    @Override
    public void delete(Apod model) {
        //TODO
        return;
    }
}
