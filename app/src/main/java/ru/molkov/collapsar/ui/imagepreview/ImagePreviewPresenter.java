package ru.molkov.collapsar.ui.imagepreview;

import android.support.annotation.NonNull;

import java.util.Date;

import ru.molkov.collapsar.data.IRepository;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

public class ImagePreviewPresenter implements ImagePreviewContract.Presenter {
    private final IRepository<Apod> mRepository;
    private final ImagePreviewContract.View mView;

    private CompositeSubscription mSubscriptions;
    private String mApodDate;

    public ImagePreviewPresenter(String apodDate, @NonNull IRepository<Apod> repository, @NonNull ImagePreviewContract.View view) {
        mApodDate = apodDate;
        mRepository = checkNotNull(repository);
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
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
                    }

                    @Override
                    public void onNext(Apod apod) {
                        mView.setPhoto(apod.getUrl());
                    }
                });
        mSubscriptions.add(subscription);
    }
}
