package com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface;

/**
 * Created by Jic on 10/4/2016.
 */

public interface RegisterView {
    void setMailFormatError();
    void setPhoneLengthError();
    void setNameLengthError();
    void setPassLengthError();
    void setComfirmPassError();
    void setMailNullError();
    void setPhoneNullError();
    void setNameNullError();
    void setPassNullError();
    void setMailExistedError();
    void setUsernameExistedError();
    void navigateToLogin();
}
