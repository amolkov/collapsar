package ru.molkov.collapsar.data.source.local;

import android.provider.BaseColumns;

import ru.molkov.collapsar.utils.Constants;

public class ApodPersistenceContract {

    private ApodPersistenceContract() {
    }

    public static abstract class ApodEntry implements BaseColumns {
        public static final String TABLE_NAME = "APOD";
        public static final String COLUMN_NAME_COPYRIGHT = "COPYRIGHT";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_EXPLANATION = "EXPLANATION";
        public static final String COLUMN_NAME_MEDIA_TYPE = "MEDIA_TYPE";
        public static final String COLUMN_NAME_TITLE = "TITLE";
        public static final String COLUMN_NAME_URL = "URL";
        public static final String COLUMN_NAME_URL_HD = "URL_HD";
        public static final String COLUMN_NAME_IMAGE_PATH = "IMAGE_PATH";
    }
}
