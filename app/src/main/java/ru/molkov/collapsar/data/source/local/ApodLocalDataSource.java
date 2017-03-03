package ru.molkov.collapsar.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Date;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.local.ApodPersistenceContract.ApodEntry;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApodLocalDataSource implements ILocalDataSource<Apod> {
    private static ApodLocalDataSource INSTANCE;
    private final BriteDatabase mDatabaseHelper;

    private ApodLocalDataSource(@NonNull Context context) {
        checkNotNull(context);

        ApodDbHelper dbHelper = new ApodDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        mDatabaseHelper.setLoggingEnabled(true);
    }

    public static ApodLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ApodLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public Observable<Apod> get(@NonNull Date date) {
        checkNotNull(date);

        String sql = String.format("SELECT %s FROM %s WHERE %s=?", TextUtils.join(",", Apod.PROJECTION), ApodEntry.TABLE_NAME, ApodEntry.COLUMN_NAME_DATE);
        return mDatabaseHelper.createQuery(ApodEntry.TABLE_NAME, sql, DateUtils.toString(date)).mapToOneOrDefault(Apod.MAPPER, null);
    }

    @Override
    public Observable<Apod> create(@NonNull Apod model) {
        checkNotNull(model);

        ContentValues values = new Apod.ValuesBuilder().apod(model).build();
        mDatabaseHelper.insert(ApodEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        return get(model.getDate());
    }

    @Override
    public Observable<Apod> update(@NonNull Apod model) {
        checkNotNull(model);

        ContentValues values = new Apod.ValuesBuilder().apod(model).build();
        String selection = ApodEntry.COLUMN_NAME_DATE + "=?";
        String[] args = {DateUtils.toString(model.getDate())};
        mDatabaseHelper.update(ApodEntry.TABLE_NAME, values, selection, args);
        return get(model.getDate());
    }

    @Override
    public void delete(@NonNull Apod model) {
        checkNotNull(model);

        String selection = ApodEntry.COLUMN_NAME_DATE + "=?";
        String[] args = {DateUtils.toString(model.getDate())};
        mDatabaseHelper.delete(ApodEntry.TABLE_NAME, selection, args);
    }

    @Override
    public void deleteAll() {
        mDatabaseHelper.delete(ApodEntry.TABLE_NAME, null);
    }
}
