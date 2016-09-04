package ru.molkov.collapsar.recentimages;

import java.util.List;

import ru.molkov.collapsar.BasePresenter;
import ru.molkov.collapsar.BaseView;
import ru.molkov.collapsar.data.model.Apod;

public interface RecentImagesContract {

    interface View extends BaseView<Presenter> {

        void showLoadedApods(List<Apod> apods);

        void showUpdatedApods(List<Apod> apods);

        void setLoadingIndicator(boolean isActive);

        void removeItemAdapter();
    }

    interface Presenter extends BasePresenter {

        void loadApods();

        void updateApods(boolean isForeUpdate);
    }
}
