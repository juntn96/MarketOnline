package com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface;


import com.jic.marketonlinev2.Model.UsersInfo;

/**
 * Created by Jic on 10/4/2016.
 */

public interface LoginView {
    void setMailError();
    void setPassError();
    void navigateToMain(UsersInfo usersInfo);
}
