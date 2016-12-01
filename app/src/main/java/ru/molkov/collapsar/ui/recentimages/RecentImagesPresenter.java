package ru.molkov.collapsar.ui.recentimages;

import java.util.Date;
import java.util.List;

import ru.molkov.collapsar.data.IRepository;
import ru.molkov.collapsar.data.core.error.RetrofitException;
import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.utils.Constants;
import ru.molkov.collapsar.utils.DateUtils;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RecentImagesPresenter implements RecentImagesContract.Presenter {
    private final IRepository<Apod> mRepository;
    private final RecentImagesContract.View mView;

    private CompositeSubscription mSubscriptions;
    private Date mLastLoadedDate;
    private boolean mFirstLoad = true;

    public RecentImagesPresenter(IRepository<Apod> repository, RecentImagesContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        refreshApods(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadApods() {
        mSubscriptions.clear();

        Date dateFrom = DateUtils.toGMT(new Date());
        if (mLastLoadedDate != null) {
            dateFrom = DateUtils.addDays(mLastLoadedDate, -1);
        }

        Subscription subscription = mRepository
                .getList(DateUtils.getDatesToLoad(dateFrom, Constants.PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Apod>>() {
                    @Override
                    public void onCompleted() {
                        mView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.i(throwable, "There was an error loading recent images");
                        mView.setLoadingIndicator(false);

                        processError(throwable);
                    }

                    @Override
                    public void onNext(List<Apod> apods) {
                        mView.showLoadedApods(apods);
                        updateLastLoadedDate(apods);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void refreshApods(boolean isForceUpdate) {
        if (mFirstLoad) {
            mView.setFirstLoadProgress(true);
            mFirstLoad = false;
        }

        if (isForceUpdate) {
            mView.setRefreshIndicator(true);
        }
        mSubscriptions.clear();

        Date dateFrom = DateUtils.toGMT(new Date());
        Subscription subscription = mRepository
                .getList(DateUtils.getDatesToLoad(dateFrom, Constants.PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Apod>>() {
                    @Override
                    public void onCompleted() {
                        mView.setFirstLoadProgress(false);
                        mView.setRefreshIndicator(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.i(throwable, "There was an error refreshing recent images");
                        mView.setRefreshIndicator(false);

                        processError(throwable);
                    }

                    @Override
                    public void onNext(List<Apod> apods) {
                        mView.showUpdatedApods(apods);
                        updateLastLoadedDate(apods);

                    }
                });
        mSubscriptions.add(subscription);
    }

    private void updateLastLoadedDate(List<Apod> apods) {
        if (apods != null && !apods.isEmpty()) {
            mLastLoadedDate = apods.get(apods.size() - 1).getDate();
        }
    }

    private void processError(Throwable throwable) {
        RetrofitException exception;
        if (throwable instanceof CompositeException) {
            //TODO fix me
            CompositeException compositeException = (CompositeException) throwable;
            exception = (RetrofitException) compositeException.getExceptions().get(0);
        } else {
            exception = (RetrofitException) throwable;
        }

        mView.showError(exception.getMessage());
    }
}
