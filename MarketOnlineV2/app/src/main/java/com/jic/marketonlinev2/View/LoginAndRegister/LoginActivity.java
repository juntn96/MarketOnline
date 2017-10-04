package com.jic.marketonlinev2.View.LoginAndRegister;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.Presenter.LoginPresenterImpl;
import com.jic.marketonlinev2.Presenter.RegisterPresenterImpl;
import com.jic.marketonlinev2.R;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.GetLoginUser;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.LoginView;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.GetRegisterUser;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.RegisterView;
import com.jic.marketonlinev2.View.MainScreen.MainActivity;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener, RegisterView{

    private CheckBox cbRemember;
    private EditText etLoginMail;
    private EditText etLoginPass;
    private Dialog guiRegister = null;
    private ProgressDialog pDialog;
    private Dialog guiForgot = null;
    private EditText forgotEmail;
    private UsersInfo user = new UsersInfo();

    private GetLoginUser getLoginUser;
    private GetRegisterUser registerUser;

    private EditText etName, etPass, etCfPass, etMail, etPhone;

    private String resMail, resPass;
    private String loginMail, loginPass;

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private final String PREF_NAME = "my_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

        etLoginMail = (EditText) findViewById(R.id.tvLoginMail);
        etLoginPass = (EditText) findViewById(R.id.tvLoginPass);
        cbRemember = (CheckBox) findViewById(R.id.cbRemember);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.btnForgot).setOnClickListener(this);

        getLoginUser = new LoginPresenterImpl(LoginActivity.this);
        registerUser = new RegisterPresenterImpl(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            userAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void setMailError() {
        etLoginMail.setError("Incorrect Email");
        pDialog.dismiss();
    }

    @Override
    public void setPassError() {
        etLoginPass.setError("Incorrect Password");
        pDialog.dismiss();
    }

    @Override
    public void navigateToMain(UsersInfo usersInfo) {
        user = usersInfo;
        pDialog.dismiss();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userInfo", user);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                loginMail = etLoginMail.getText().toString().trim();
                loginPass = etLoginPass.getText().toString().trim();
                waitDialog();
                getLoginUser.getUser(loginMail, loginPass);
                break;
            }
            case R.id.btnRegister: {
                guiRegister = new Dialog(this);
                guiRegister.setContentView(R.layout.fragment_register);
                guiRegister.findViewById(R.id.btnCreate).setOnClickListener(this);
                guiRegister.findViewById(R.id.btnCancel).setOnClickListener(this);
                guiRegister.show();
                break;
            }
            case R.id.btnForgot: {
                guiForgot = new Dialog(this);
                guiForgot.setContentView(R.layout.fragment_forgot);
                guiForgot.findViewById(R.id.btnConfirmRs).setOnClickListener(this);
                guiForgot.findViewById(R.id.btnCancelRs).setOnClickListener(this);
                guiForgot.show();
                break;
            }
            case R.id.btnCreate: {
                etName = (EditText) guiRegister.findViewById(R.id.etResName);
                etPass = (EditText) guiRegister.findViewById(R.id.etResPass);
                etCfPass = (EditText) guiRegister.findViewById(R.id.etReResPass);
                etPhone = (EditText) guiRegister.findViewById(R.id.etResPhone);
                etMail = (EditText) guiRegister.findViewById(R.id.etResMail);

                resMail = etMail.getText().toString();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                resPass = etPass.getText().toString();
                String cfPass = etCfPass.getText().toString();

                waitDialog();

                registerUser.getRegister(resMail, name, phone, resPass, cfPass);

                break;
            }
            case R.id.btnCancel: {
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Are you want to cancel ?");
                alert.setPositiveButton("\t\t\t\t\t\tNo\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.setNegativeButton("\t\t\t\t\t\tYes\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guiRegister.dismiss();
                    }
                });
                alert.create().show();
                break;
            }
            case R.id.btnConfirmRs: {
                forgotEmail = (EditText) guiForgot.findViewById(R.id.etForgotMail);
                userAuth.sendPasswordResetEmail(forgotEmail.getText().toString());
                Toast.makeText(LoginActivity.this, "Please check your email to reset password", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.btnCancelRs: {
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Are you want to cancel ?");
                alert.setPositiveButton("\t\t\t\t\t\tNo\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.setNegativeButton("\t\t\t\t\t\tYes\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guiForgot.dismiss();
                    }
                });
                alert.create().show();
                break;
            }
        }
    }

    public void waitDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    @Override
    public void setMailFormatError() {
        etMail.setError("Invalid email");
        pDialog.dismiss();
    }

    @Override
    public void setPhoneLengthError() {
        etPhone.setError("Invalid phone number");
        pDialog.dismiss();
    }

    @Override
    public void setNameLengthError() {
        etName.setError("Username must be 6-16 characters");
        pDialog.dismiss();
    }

    @Override
    public void setPassLengthError() {
        etPass.setError("Your Password must be at least 6 character");
        pDialog.dismiss();
    }

    @Override
    public void setComfirmPassError() {
        etCfPass.setError("Password must same");
        pDialog.dismiss();
    }

    @Override
    public void setMailNullError() {
        etMail.setError("Please enter your Email");
        pDialog.dismiss();
    }

    @Override
    public void setPhoneNullError() {
        etPhone.setError("Please enter your Phone number");
        pDialog.dismiss();
    }

    @Override
    public void setNameNullError() {
        etName.setError("Please enter your Username");
        pDialog.dismiss();
    }

    @Override
    public void setPassNullError() {
        etPass.setError("Please enter your Password");
        pDialog.dismiss();
    }

    @Override
    public void setMailExistedError() {
        etMail.setError("Email address already existed");
        pDialog.dismiss();
    }

    @Override
    public void setUsernameExistedError() {
        etName.setError("Username already existed ");
        pDialog.dismiss();
    }

    @Override
    public void navigateToLogin() {
        pDialog.dismiss();
        guiRegister.dismiss();
        Toast.makeText(LoginActivity.this, "Register Success!!! Thank You", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoringPreferences();
    }


    @Override
    protected void onPause() {
        super.onPause();
        savingPreferences();
    }

    public void savingPreferences() {
        //tạo đối tượng getSharedPreferences
        SharedPreferences pre = getSharedPreferences
                (PREF_NAME, MODE_PRIVATE);
        //tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor = pre.edit();
        String saveMail = etLoginMail.getText().toString();
        String savePass = etLoginPass.getText().toString();
        boolean bchk = cbRemember.isChecked();
        if (!bchk) {
            //xóa mọi lưu trữ trước đó
            editor.clear();
        } else {
            //lưu vào editor
            editor.putString("mail", saveMail);
            editor.putString("pass", savePass);
            editor.putBoolean("checked", bchk);
        }
        //chấp nhận lưu xuống file
        editor.commit();
    }

    public void restoringPreferences() {
        SharedPreferences pre = getSharedPreferences
                (PREF_NAME, MODE_PRIVATE);
        //lấy giá trị checked ra, nếu không thấy thì giá trị mặc định là false
        boolean bchk = pre.getBoolean("checked", false);
        if (bchk) {
            //lấy user, pwd, nếu không thấy giá trị mặc định là rỗng
            String rememberMail = pre.getString("mail", "");
            String rememberPass = pre.getString("pass", "");
            etLoginMail.setText(rememberMail);
            etLoginPass.setText(rememberPass);
        }
        cbRemember.setChecked(bchk);
    }
}
