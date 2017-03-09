package ru.molkov.collapsar.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApodDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Apods.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_APOD_TABLE = "CREATE TABLE " + ApodPersistenceContract.ApodEntry.TABLE_NAME
            + " ("
            + ApodPersistenceContract.ApodEntry._ID + INTEGER_TYPE + " PRIMARY KEY,"
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_COPYRIGHT + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_EXPLANATION + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_MEDIA_TYPE + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL_HD + TEXT_TYPE + COMMA_SEP
            + ApodPersistenceContract.ApodEntry.COLUMN_NAME_IMAGE_PATH + TEXT_TYPE
            + " )";

    public ApodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_APOD_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
