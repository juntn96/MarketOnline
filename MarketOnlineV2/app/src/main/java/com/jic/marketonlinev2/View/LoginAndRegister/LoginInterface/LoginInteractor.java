package com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface;

import com.jic.marketonlinev2.Model.LoginModel.OnCheckUserLogin;
import com.jic.marketonlinev2.Model.UsersInfo;

/**
 * Created by Jic on 10/4/2016.
 */

public interface LoginInteractor {
    void login(String mail, String pass, OnCheckUserLogin loginFishListener);
}
