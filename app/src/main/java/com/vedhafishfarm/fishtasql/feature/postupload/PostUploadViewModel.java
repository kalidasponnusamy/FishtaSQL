package com.vedhafishfarm.fishtasql.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {

    private Repository repository;
    public PostUploadViewModel (Repository repository) {
        this.repository = repository;
    }

    public LiveData <GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        return this.repository.uploadPost(multipartBody, isCoverOrProfileImage);
    }

}
