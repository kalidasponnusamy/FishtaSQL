package com.vedhafishfarm.fishtasql.feature.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vedhafishfarm.fishtasql.data.Repository;
import com.vedhafishfarm.fishtasql.model.search.SearchResponse;

import java.util.Map;

public class SeachViewModel extends ViewModel {
    private Repository repository;

    public SeachViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<SearchResponse> search(Map<String, String> params){
        return repository.search(params);
    }
}
