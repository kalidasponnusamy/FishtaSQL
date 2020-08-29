package com.vedhafishfarm.fishtasql.feature.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.feature.profile.ProfileActivity;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.model.friend.FriendResponse;
import com.vedhafishfarm.fishtasql.model.post.PostResponse;

import java.util.Map;

public class MainViewModel extends ViewModel {

    private final Repository repository;

    private MutableLiveData<FriendResponse> friends = null;

    public MainViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<FriendResponse> loadFriends(String uid) {
        if (friends == null){
            friends = repository.loadFriends(uid);
        }
        return  friends;
    }

    public LiveData <GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return repository.performOperation(performAction);
    }

    public LiveData <PostResponse> getNewsFeed(Map<String, String> params) {
        return repository.getNewsFeed(params);
    }
}
