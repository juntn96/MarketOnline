package com.jic.marketonlinev2.Model.RegisterModel;

/**
 * Created by Jic on 10/4/2016.
 */

public interface OnCheckUserRegister {
    void onMailFormatError();
    void onPhoneLengthError();
    void onNameLengthError();
    void onPassLengthError();
    void onComfirmPassError();
    void onMailNullError();
    void onPhoneNullError();
    void onNameNullError();
    void onPassNullError();
    void onMailExistedError();
    void onUsernameExistedError();
    void onRegisterSuccess();
}
