package com.jic.marketonlinev2.Model.LoginModel;

import com.jic.marketonlinev2.Model.UsersInfo;

/**
 * Created by Jic on 10/4/2016.
 */

public interface OnCheckUserLogin {
    void onMailError();
    void onPassWordError();
    void onSuccess(UsersInfo usersInfo);
}
