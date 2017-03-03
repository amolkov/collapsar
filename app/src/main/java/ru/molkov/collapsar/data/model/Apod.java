package ru.molkov.collapsar.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import ru.molkov.collapsar.data.model.meta.ApodMeta;

public class Apod implements ApodMeta, Serializable {

    @SerializedName("copyright")
    private final String mCopyright;

    @SerializedName("date")
    private final Date mDate;

    @SerializedName("explanation")
    private final String mExplanation;

    @SerializedName("media_type")
    private final String mMediaType;

    @SerializedName("title")
    private final String mTitle;

    @SerializedName("url")
    private final String mUrl;

    @SerializedName("hdurl")
    private final String mUrlHd;

    private boolean hasFadedIn = false;

    public Apod(String copyright, Date date, String explanation, String mediaType, String title, String url, String urlHd) {
        mCopyright = copyright;
        mDate = date;
        mExplanation = explanation;
        mMediaType = mediaType;
        mTitle = title;
        mUrl = url;
        mUrlHd = urlHd;
    }

    public String getCopyright() {
        return mCopyright;
    }

    public Date getDate() {
        return new Date(mDate.getTime());
    }

    public String getExplanation() {
        return mExplanation;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getUrlHd() {
        return mUrlHd;
    }

    public boolean isHasFadedIn() {
        return hasFadedIn;
    }

    public void setHasFadedIn(boolean hasFadedIn) {
        this.hasFadedIn = hasFadedIn;
    }
}