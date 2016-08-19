package com.obabichev.technomessenger.presenter.fragment;

import android.util.Log;

import com.obabichev.technomessenger.App;
import com.obabichev.technomessenger.cleanmvp.presenter.fragment.BaseFragmentPresenter;
import com.obabichev.technomessenger.interactor.ChannelInteractor;
import com.obabichev.technomessenger.interactor.RequestInteractor;
import com.obabichev.technomessenger.interactor.ResponseInteractor;
import com.obabichev.technomessenger.mapi.channel.ChannelListRequest;
import com.obabichev.technomessenger.model.Channel;
import com.obabichev.technomessenger.presenter.activity.OnBackPressedListener;
import com.obabichev.technomessenger.repository.UserRepository;
import com.obabichev.technomessenger.view.activity.MainView;
import com.obabichev.technomessenger.view.fragment.ChannelsListView;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by olegchuikin on 16/08/16.
 */

public class ChannelsListPresenter extends BaseFragmentPresenter<ChannelsListView, MainView> {

    @Inject
    RequestInteractor requestInteractor;

    @Inject
    ResponseInteractor responseInteractor;

    @Inject
    ChannelInteractor channelInteractor;

    @Inject
    UserRepository userRepository;

    private final OnBackPressedListener onBackPressedListener = new OnBackPressedListener() {
        @Override
        public boolean onBackPressed() {
            if (view.isCreateNewChannelState()) {
                view.switchToChannelsListState();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        view.getActivityView().showActionBar();
        view.getActivityView().setOnBackPressedListener(onBackPressedListener);

        startObservingViewEvents();

        channelInteractor.getChannels().subscribe(new Action1<List<Channel>>() {
            @Override
            public void call(List<Channel> channels) {
                view.showChannelsList(channels);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d(App.FILTER_TAG, throwable.getMessage());
            }
        });
    }

    private void sendChannelsListRequest() {
        ChannelListRequest request = new ChannelListRequest();
        request.setCid(userRepository.getUserId());
        request.setSid(App.sid);
        requestInteractor.sendMessage(request);
    }

    @Override
    public void onPause() {
        super.onPause();

        view.getActivityView().unsetOnBackPressedListener();
    }

    private void startObservingViewEvents() {
        view.getAddMenuItemClicks().subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.switchToCreateNewChannelState();
            }
        });

        view.getCompleteMenuItemClicks().subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.switchToChannelsListState();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
