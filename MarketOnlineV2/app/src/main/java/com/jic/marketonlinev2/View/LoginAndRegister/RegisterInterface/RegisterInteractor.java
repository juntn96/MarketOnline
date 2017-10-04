package com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface;

import com.jic.marketonlinev2.Model.RegisterModel.OnCheckUserRegister;

/**
 * Created by Jic on 10/4/2016.
 */

public interface RegisterInteractor {
    void register(String mail, String name, String phone, String pass, String cfPass, OnCheckUserRegister userRegister);
}
