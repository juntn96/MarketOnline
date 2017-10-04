package com.jic.marketonlinev2.Model.RegisterModel;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.View.LoginAndRegister.RegisterInterface.RegisterInteractor;

/**
 * Created by Jic on 10/4/2016.
 */

public class RegisterInteractorImpl implements RegisterInteractor {

    private DatabaseReference database;
    private FirebaseAuth userAuth;
    private UserProfileChangeRequest userProfileChangeRequest;
    private FirebaseUser firebaseUser;
    private int countChild = 0;
    private boolean existedName;

    @Override
    public void register(final String mail, final String name, final String phone, final String pass, String cfPass, final OnCheckUserRegister userRegister) {
        boolean error = false;
        database = FirebaseDatabase.getInstance().getReference().child("USERS");
        userAuth = FirebaseAuth.getInstance();
        countChild = 0;
        if (TextUtils.isEmpty(mail)) {
            userRegister.onMailNullError();
            error = true;
        }
        if (TextUtils.isEmpty(name)) {
            userRegister.onNameNullError();
            error = true;
        }
        if (TextUtils.isEmpty(phone)) {
            userRegister.onPhoneNullError();
            error = true;
        }
        if (TextUtils.isEmpty(pass)) {
            userRegister.onPassNullError();
            error = true;
        }
        if (name.length() < 6 || name.length() > 16) {
            userRegister.onNameLengthError();
            error = true;
        }
        if (phone.length() < 10 || phone.length() > 11) {
            userRegister.onPhoneLengthError();
            error = true;
        }
        if (!pass.equals(cfPass)) {
            userRegister.onComfirmPassError();
            error = true;
        }
        if (pass.length() < 6) {
            userRegister.onPassLengthError();
            error = true;
        }
        if (!error) {
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long numberChild = dataSnapshot.getChildrenCount();
                    database.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            countChild++;
                            Log.d("Warning number child", "" + numberChild);
                            String getName = dataSnapshot.child("name").getValue() + "";
                            Log.d("Warning name", "" + getName);
                            if (getName.equals(name)) {
                                existedName = true;
                            }
                            Log.d("Warning exist name 1", "" + existedName);
                            if (countChild == numberChild) {
                                if (!existedName) {
                                    Log.d("Warning exist name 2", "" + existedName);
                                    saveUser(mail, pass, name, phone, userRegister);
                                } else {
                                    existedName = false;
                                    userRegister.onUsernameExistedError();
                                }
                                countChild = 0;
                            }
                            Log.d("Warning count", "" + countChild);
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
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void saveUser(final String mail, String pass, final String name, final String phone, final OnCheckUserRegister userRegister) {
        userAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d("Warning!!!", "" + task.getException().getMessage());
                if (task.isSuccessful()) {
                    userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.updateProfile(userProfileChangeRequest);
                    UsersInfo user = new UsersInfo(name, phone, mail, "");
                    database.child(userAuth.getCurrentUser().getUid()).setValue(user);
                    userRegister.onRegisterSuccess();
                } else {
                    if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        userRegister.onMailExistedError();
                    }
                    if (task.getException().getMessage().equals("The email address is badly formatted.")) {
                        userRegister.onMailFormatError();
                    }
                }
            }
        });
    }
}


