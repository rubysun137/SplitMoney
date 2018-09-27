package com.ruby.splitmoney;

import com.ruby.splitmoney.util.BasePresenter;
import com.ruby.splitmoney.util.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

        void transToMain();

        void transToQuickSplit();

    }
}
