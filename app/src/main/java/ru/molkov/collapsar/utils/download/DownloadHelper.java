package ru.molkov.collapsar.utils.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import ru.molkov.collapsar.R;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.Constants;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.FileUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class DownloadHelper {
    private static DownloadHelper INSTANCE;
    public static final int DOWNLOAD_ACTION = 1;

    private DownloadManager mDownloadManager;

    public static DownloadHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DownloadHelper(context);
        }
        return INSTANCE;
    }

    private DownloadHelper(@NonNull Context context) {
        checkNotNull(context);
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void addDownload(@NonNull Context context, @NonNull Apod apod) {
        if (FileUtils.createDownloadPath()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apod.getUrl()))
                    .setTitle(apod.getTitle())
                    .setDescription(context.getString(R.string.message_downloading))
                    .setDestinationInExternalPublicDir(Constants.DOWNLOAD_IMAGE_PATH, createDestinationDir(apod));
            request.allowScanningByMediaScanner();
            mDownloadManager.enqueue(request);
        }
    }

    private String createDestinationDir(Apod apod) {
        StringBuilder sb = new StringBuilder();
        sb.append(apod.getTitle());
        sb.append("(");
        sb.append(DateUtils.friendlyFormat(apod.getDate(), false));
        sb.append(")");
        sb.append(Constants.DOWNLOAD_IMAGE_FORMAT);
        return sb.toString();
    }
}
