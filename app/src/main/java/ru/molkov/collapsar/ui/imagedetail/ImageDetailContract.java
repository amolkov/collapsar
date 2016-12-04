package ru.molkov.collapsar.ui.imagedetail;

import android.app.Activity;

import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface ImageDetailContract {

    interface View extends BaseView<Presenter> {

        void setPhoto(String url);

        void setTitle(String title);

        void setSubtitle(String subtitle);

        void setExplanation(String explanation);

        void setCopyright(String copyright);

        void showError(String error);
    }

    interface Presenter extends BasePresenter {

        void openApod();

        void downloadApod(Activity activity);

        void shareApod(Activity activity);
    }
}
