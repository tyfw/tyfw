package com.example.tyfw.ui.search;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class SearchFormState {
    @Nullable
    private Integer searchStringError;
    private boolean isDataValid;

    SearchFormState(@Nullable Integer searchStringError) {
        this.searchStringError = searchStringError;
        this.isDataValid = false;
    }

    SearchFormState(boolean isDataValid) {
        this.searchStringError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getFirstNameError() {
        return searchStringError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}