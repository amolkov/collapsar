package ru.molkov.collapsar.ui.imagepreview;


import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface ImagePreviewContract {

    interface View extends BaseView<ImagePreviewContract.Presenter> {

        void setPhoto(String url);
    }

    interface Presenter extends BasePresenter {

        void openApod();
    }
}
