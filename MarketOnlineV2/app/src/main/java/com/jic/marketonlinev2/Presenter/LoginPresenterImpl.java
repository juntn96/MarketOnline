package com.jic.marketonlinev2.Presenter;

import com.jic.marketonlinev2.Model.LoginModel.LoginInteractorImpl;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.LoginInteractor;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.GetLoginUser;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.LoginView;
import com.jic.marketonlinev2.Model.LoginModel.OnCheckUserLogin;

/**
 * Created by Jic on 10/4/2016.
 */

public class LoginPresenterImpl implements GetLoginUser, OnCheckUserLogin {

    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractorImpl();
    }

    @Override
    public void getUser(String mail, String pass) {
        loginInteractor.login(mail, pass, this);
    }

    @Override
    public void onMailError() {
        loginView.setMailError();
    }

    @Override
    public void onPassWordError() {
        loginView.setPassError();
    }

    @Override
    public void onSuccess(UsersInfo usersInfo) {
        loginView.navigateToMain(usersInfo);
    }

}
