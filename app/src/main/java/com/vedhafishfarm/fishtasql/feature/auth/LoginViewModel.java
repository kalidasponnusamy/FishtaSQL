package com.vedhafishfarm.fishtasql.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private Repository repository;

    public LoginViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo){
        return repository.login(userInfo);
    }
}
