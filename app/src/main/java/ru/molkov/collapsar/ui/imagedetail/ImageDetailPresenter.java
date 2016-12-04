package ru.molkov.collapsar.ui.imagedetail;

import android.app.Activity;

import java.util.Date;

import ru.molkov.collapsar.data.IRepository;
import ru.molkov.collapsar.data.core.error.RetrofitException;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.ui.ShareTask;
import ru.molkov.collapsar.utils.DateUtils;
import ru.molkov.collapsar.utils.ImageUtils;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ImageDetailPresenter implements ImageDetailContract.Presenter {
    private final IRepository<Apod> mRepository;
    private final ImageDetailContract.View mView;

    private CompositeSubscription mSubscriptions;
    private String mApodDate;
    private Apod mApod;

    public ImageDetailPresenter(String apodDate, IRepository<Apod> repository, ImageDetailContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
        mApodDate = apodDate;
    }

    @Override
    public void subscribe() {
        openApod();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void openApod() {
        mSubscriptions.clear();

        Date date = DateUtils.toDate(mApodDate);
        Subscription subscription = mRepository
                .get(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Apod>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.i(throwable, "There was an error loading image");

                        RetrofitException exception = (RetrofitException) throwable;
                        mView.showError(exception.getMessage());
                    }

                    @Override
                    public void onNext(Apod apod) {
                        mApod = apod;
                        showApod(apod);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void downloadApod(Activity activity) {

    }

    @Override
    public void shareApod(Activity activity) {
        new ShareTask(activity, mApod).execute();
    }

    private void showApod(Apod apod) {
        mView.setPhoto(apod.getMediaType().equalsIgnoreCase("image") ? apod.getUrl() : ImageUtils.getThumbnailUrl(apod.getUrl()));
        mView.setTitle(apod.getTitle());
        mView.setSubtitle(DateUtils.friendlyFormat(apod.getDate(), true));
        mView.setExplanation(apod.getExplanation());
        mView.setCopyright(formatCopyright(apod));
    }

    private String formatCopyright(Apod apod) {
        StringBuilder builder = new StringBuilder();
        if (apod.getCopyright() != null) {
            String copyright = apod.getCopyright().replace("\n", " ").replace("\r", " ");
            builder.append("by ").append(copyright);
        }
        return builder.toString();
    }
}
