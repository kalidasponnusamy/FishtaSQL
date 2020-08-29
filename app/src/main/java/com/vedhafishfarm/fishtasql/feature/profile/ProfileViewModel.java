package com.vedhafishfarm.fishtasql.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.model.profile.ProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {
    private Repository repository;
    public ProfileViewModel (Repository repository) {
        this.repository = repository;
    }

    public  LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        return repository.fetchProfileInfo(params);
    }

    public LiveData <GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        return repository.uploadPost(multipartBody, isCoverOrProfileImage);
    }

    public LiveData <GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return repository.performOperation(performAction);
    }

}
