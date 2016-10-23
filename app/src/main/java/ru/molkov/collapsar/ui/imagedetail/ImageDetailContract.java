package ru.molkov.collapsar.ui.imagedetail;

import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface ImageDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setSubtitle(String subtitle);

        void setExplanation(String explanation);

        void showError(String error);
    }

    interface Presenter extends BasePresenter {

        void openApod();
    }
}
