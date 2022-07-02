package com.example.tyfw.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<SearchFormState> searchFormState = new MutableLiveData<>();

    public SearchViewModel() {
    }
}