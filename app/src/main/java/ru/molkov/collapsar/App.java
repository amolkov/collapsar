package ru.molkov.collapsar;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext() {
        return mContext;
    }
}
