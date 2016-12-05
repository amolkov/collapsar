package ru.molkov.collapsar.ui;


import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ImageUtils;

public class ShareTask extends AsyncTask<Void, Void, File> {
    private final Activity mActivity;
    private final Apod mApod;

    public ShareTask(Activity activity, Apod apod) {
        mActivity = activity;
        mApod = apod;
    }

    @Override
    protected File doInBackground(Void... voids) {
        try {
            String url = mApod.getMediaType().equalsIgnoreCase("image") ? mApod.getUrl() : ImageUtils.getThumbnailUrl(mApod.getUrl());
            return Glide
                    .with(mActivity)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(File file) {
        if (file == null) {
            return;
        }

        Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getString(R.string.share_authority), file);
        ShareCompat.IntentBuilder.from(mActivity)
                .setText(getShareText())
                .setType("image/jpeg")
                .setSubject(mApod.getTitle())
                .setStream(uri)
                .startChooser();
    }

    private String getShareText() {
        return new StringBuilder()
                .append("“")
                .append(mApod.getTitle())
                .append("” ")
                .append(formatCopyright())
                .append(" ")
                .append(formatDate())
                .append("\n")
                .append(formatUrl())
                .toString();
    }

    private String formatCopyright() {
        StringBuilder builder = new StringBuilder();
        if (mApod.getCopyright() != null) {
            String copyright = mApod.getCopyright().replace("\n", " ").replace("\r", " ");
            builder.append("by ").append(copyright);
        }
        return builder.toString();
    }

    private String formatUrl() {
        if (mApod.getMediaType().equalsIgnoreCase("image")) {
            return "";
        }
        return mApod.getUrl();
    }

    private String formatDate() {
        return "published on " + DateUtils.friendlyFormat(mApod.getDate(), false);
    }
}
