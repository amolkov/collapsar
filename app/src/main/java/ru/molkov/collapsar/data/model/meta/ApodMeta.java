package ru.molkov.collapsar.data.model.meta;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.data.source.local.ApodPersistenceContract;
import ru.molkov.collapsar.data.source.local.Db;
import ru.molkov.collapsar.utils.DateUtils;
import rx.functions.Func1;

public interface ApodMeta {

    String[] PROJECTION = {
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_COPYRIGHT,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_DATE,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_EXPLANATION,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_MEDIA_TYPE,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_TITLE,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL,
            ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL_HD
    };

    Func1<Cursor, Apod> MAPPER = cursor -> {
        String copyright = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_COPYRIGHT);
        String date = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_DATE);
        String explanation = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_EXPLANATION);
        String mediaType = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_MEDIA_TYPE);
        String title = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_TITLE);
        String url = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL);
        String urlHd = Db.getString(cursor, ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL_HD);

        Date parsedDate = DateUtils.toDate(date);
        return new Apod(copyright, parsedDate, explanation, mediaType, title, url, urlHd);
    };

    class ValuesBuilder {
        private final ContentValues values = new ContentValues();

        public ValuesBuilder copyright(String copyright) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_COPYRIGHT, copyright);
            return this;
        }

        public ValuesBuilder date(Date date) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_DATE, DateUtils.toString(date));
            return this;
        }

        public ValuesBuilder explanation(String explanation) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_EXPLANATION, explanation);
            return this;
        }

        public ValuesBuilder mediaType(String mediaType) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_MEDIA_TYPE, mediaType);
            return this;
        }

        public ValuesBuilder title(String title) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_TITLE, title);
            return this;
        }

        public ValuesBuilder url(String url) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL, url);
            return this;
        }

        public ValuesBuilder urlHd(String urlHd) {
            values.put(ApodPersistenceContract.ApodEntry.COLUMN_NAME_URL_HD, urlHd);
            return this;
        }

        public ValuesBuilder apod(Apod apod) {
            return copyright(apod.getCopyright())
                    .date(apod.getDate())
                    .explanation(apod.getExplanation())
                    .mediaType(apod.getMediaType())
                    .title(apod.getTitle())
                    .url(apod.getUrl())
                    .urlHd(apod.getUrlHd());
        }

        public ContentValues build() {
            return values;
        }
    }
}
