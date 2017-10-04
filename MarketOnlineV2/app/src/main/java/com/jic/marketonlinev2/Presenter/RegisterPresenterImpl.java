package com.jic.marketonlinev2.Presenter;

import com.jic.marketonlinev2.Model.RegisterModel.OnCheckUserRegister;
import com.jic.marketonlinev2.Model.RegisterModel.RegisterInteractorImpl;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.GetRegisterUser;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.RegisterInteractor;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.RegisterView;

/**
 * Created by Jic on 10/4/2016.
 */

public class RegisterPresenterImpl implements GetRegisterUser, OnCheckUserRegister {

    private RegisterView registerView;
    private RegisterInteractor registerInteractor;

    public RegisterPresenterImpl(RegisterView registerView) {
        this.registerView = registerView;
        this.registerInteractor = new RegisterInteractorImpl();
    }

    @Override
    public void getRegister(String mail, String name, String phone, String pass, String cfPass) {
        registerInteractor.register(mail, name, phone, pass, cfPass, this);
    }

    @Override
    public void onMailFormatError() {
        registerView.setMailFormatError();
    }

    @Override
    public void onPhoneLengthError() {
        registerView.setPhoneLengthError();
    }

    @Override
    public void onNameLengthError() {
        registerView.setNameLengthError();
    }

    @Override
    public void onPassLengthError() {
        registerView.setPassLengthError();
    }

    @Override
    public void onComfirmPassError() {
        registerView.setComfirmPassError();
    }

    @Override
    public void onMailNullError() {
        registerView.setMailNullError();
    }

    @Override
    public void onPhoneNullError() {
        registerView.setPhoneNullError();
    }

    @Override
    public void onNameNullError() {
        registerView.setNameNullError();
    }

    @Override
    public void onPassNullError() {
        registerView.setPassNullError();
    }

    @Override
    public void onMailExistedError() {registerView.setMailExistedError();}

    @Override
    public void onUsernameExistedError() {registerView.setUsernameExistedError();}

    @Override
    public void onRegisterSuccess() {
        registerView.navigateToLogin();
    }
}
