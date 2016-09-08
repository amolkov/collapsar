package ru.molkov.collapsar.ui.recentimages;

import java.util.List;

import ru.molkov.collapsar.data.model.Apod;
import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface RecentImagesContract {

    interface View extends BaseView<Presenter> {

        void showLoadedApods(List<Apod> apods);

        void showUpdatedApods(List<Apod> apods);

        void setLoadingIndicator(boolean isActive);

        void setRefreshIndicator(boolean isActive);
    }

    interface Presenter extends BasePresenter {

        void loadApods();

        void refreshApods(boolean isForeUpdate);
    }
}
