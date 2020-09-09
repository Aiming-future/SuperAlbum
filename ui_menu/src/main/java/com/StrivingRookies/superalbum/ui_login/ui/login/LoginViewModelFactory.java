package com.StrivingRookies.superalbum.ui_login.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.StrivingRookies.superalbum.ui_login.data.LoginDataSource;
import com.StrivingRookies.superalbum.ui_login.data.LoginRepository;


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom ( LoginViewModel.class )) {
            return (T) new LoginViewModel ( LoginRepository.getInstance ( new LoginDataSource () ) );
        } else {
            throw new IllegalArgumentException ( "Unknown ViewModel class" );
        }
    }
}
