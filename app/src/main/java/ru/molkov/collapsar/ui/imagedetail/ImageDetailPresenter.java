package ru.molkov.collapsar.ui.imagedetail;

import java.util.Date;

import ru.molkov.collapsar.data.IRepository;
import ru.molkov.collapsar.data.core.error.RetrofitException;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.DateUtils;
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
                        showApod(apod);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void showApod(Apod apod) {
        mView.setTitleContainerColor(apod.getUrl());
        mView.setTitle(apod.getTitle());
        mView.setDate(DateUtils.friendlyFormat(apod.getDate()));
        mView.setExplanation(apod.getExplanation());

        if (apod.getCopyright() != null) {
            mView.setCopyright(apod.getCopyright());
        }
    }
}
