package com.ruby.splitmoney;

import com.ruby.splitmoney.objects.Event;
import com.ruby.splitmoney.util.BasePresenter;
import com.ruby.splitmoney.util.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {
        void setToolBarTitle(String title);

        void showNewTheme();

    }

    interface Presenter extends BasePresenter {
        void transToHome();

        void transToSpend();

        void transToSplit(boolean transToFriend);

        void transToQuickSplit();

        void changeTheme();

        void transToFriendDetailPage(String friendName);

        void transToAddListPage();

        void transToAddGroupPage();

        void transToListDetailPage(Event event);

        void transToGroupDetailPage(String groupId);

    }
}
