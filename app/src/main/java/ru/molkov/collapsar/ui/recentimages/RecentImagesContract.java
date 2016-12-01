package ru.molkov.collapsar.ui.recentimages;

import java.util.List;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface RecentImagesContract {

    interface View extends BaseView<Presenter> {

        void showLoadedApods(List<Apod> apods);

        void showUpdatedApods(List<Apod> apods);

        void setFirstLoadProgress(boolean isActive);

        void setLoadingIndicator(boolean isActive);

        void setRefreshIndicator(boolean isActive);

        void showError(String error);
    }

    interface Presenter extends BasePresenter {

        void loadApods();

        void refreshApods(boolean isForeUpdate);
    }
}
