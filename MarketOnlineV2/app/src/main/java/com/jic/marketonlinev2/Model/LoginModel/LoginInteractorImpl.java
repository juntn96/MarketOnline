package com.jic.marketonlinev2.Model.LoginModel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginInterface.LoginInteractor;

/**
 * Created by Jic on 10/4/2016.
 */

public class LoginInteractorImpl implements LoginInteractor {

    private UsersInfo user;

    public LoginInteractorImpl() {

    }

    public UsersInfo getUser() {
        return user;
    }

    public void setUser(UsersInfo user) {
        this.user = user;
    }

    @Override
    public void login(final String mail, String pass, final OnCheckUserLogin checkUser) {
        boolean error = false;
        if (TextUtils.isEmpty(mail)) {
            checkUser.onMailError();
            error = true;
        }
        if (TextUtils.isEmpty(pass)) {
            checkUser.onPassWordError();
            error = true;
        }
        if (!error) {
            FirebaseAuth userAuth = FirebaseAuth.getInstance();
            userAuth.signInWithEmailAndPassword(mail.trim(), pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Log.d("Warning!!!", "dddddddoooooooooooooooo");
                        getUser(mail, checkUser);
                    } else {
                        checkUser.onMailError();
                        checkUser.onPassWordError();
                    }
                }
            });
        }
    }

    public void getUser(final String mail, final OnCheckUserLogin checkUser) {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("USERS");
        data.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String getMail = dataSnapshot.child("mail").getValue()+"";
                //Log.d("Warning!!!", "" + getMail);
                if (getMail.equals(mail)) {
                    String getName = dataSnapshot.child("name").getValue()+"";
                    String getPhone = dataSnapshot.child("phone").getValue()+"";
                    String getAvatar = dataSnapshot.child("avatar").getValue()+"";
                    setUser(new UsersInfo(getName, getPhone, getMail, getAvatar));
                    checkUser.onSuccess(getUser());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
