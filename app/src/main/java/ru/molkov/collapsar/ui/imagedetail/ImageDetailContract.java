package ru.molkov.collapsar.ui.imagedetail;

import ru.molkov.collapsar.ui.base.BasePresenter;
import ru.molkov.collapsar.ui.base.BaseView;

public interface ImageDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitleContainerColor(String url);

        void setTitle(String title);

        void setDate(String date);

        void setExplanation(String explanation);

        void setCopyright(String copyright);
    }

    interface Presenter extends BasePresenter {

        void openApod();
    }
}
