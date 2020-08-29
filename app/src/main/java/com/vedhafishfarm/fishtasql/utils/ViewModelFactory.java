package com.vedhafishfarm.fishtasql.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.data.remote.ApiClient;
import com.vedhafishfarm.fishtasql.data.remote.ApiService;
import com.vedhafishfarm.fishtasql.feature.auth.LoginViewModel;
import com.vedhafishfarm.fishtasql.feature.homepage.MainViewModel;
import com.vedhafishfarm.fishtasql.feature.postupload.PostUploadViewModel;
import com.vedhafishfarm.fishtasql.feature.profile.ProfileViewModel;
import com.vedhafishfarm.fishtasql.feature.search.SeachViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(repository);
        }else if(modelClass.isAssignableFrom(ProfileViewModel.class)){
            return (T) new ProfileViewModel(repository);
        }
        else if(modelClass.isAssignableFrom(PostUploadViewModel.class)) {
            return (T) new PostUploadViewModel(repository);
        } else if(modelClass.isAssignableFrom(SeachViewModel.class)) {
            return (T) new SeachViewModel(repository);
        } else if(modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("View Model not Found !!");
    }
}
