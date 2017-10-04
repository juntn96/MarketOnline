package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jic.marketonlinev2.Model.StoreInfo;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    //private UsersInfo usersInfo;
    private EditText etEditName, etEditMail, etEditPhone;
    private Dialog changePassDialog;
    //private Button btnChangePass, btnSave;


    private FirebaseAuth userAuth;
    private UserProfileChangeRequest userProfileChangeRequest;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    private EditText etNewPass, etCfNewPass;

    private StoreInfo storeInfo;

    private boolean clickedBtnName = false;
    private boolean clickedBtnMail = false;
    private boolean clickedBtnPhone = false;

    private int countChild;
    private boolean existedName;
    private String oldName;

    //private ProgressDialog pDialog;

    private ImageButton btnEditName, btnEditMail, btnEditPhone;


    public InformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_information, container, false);
        //usersInfo = getArguments().getParcelable("user");

        etEditName = (EditText) rootView.findViewById(R.id.etEditName);
        etEditMail = (EditText) rootView.findViewById(R.id.etEditMail);
        etEditPhone = (EditText) rootView.findViewById(R.id.etEditPhone);


        etEditPhone.setEnabled(false);
        etEditMail.setEnabled(false);
        etEditName.setEnabled(false);


        rootView.findViewById(R.id.btnChangePass).setOnClickListener(this);

        btnEditName = (ImageButton) rootView.findViewById(R.id.btnEditName);
        btnEditMail = (ImageButton) rootView.findViewById(R.id.btnEditMail);
        btnEditPhone = (ImageButton) rootView.findViewById(R.id.btnEditPhone);

        btnEditName.setOnClickListener(this);
        btnEditMail.setOnClickListener(this);
        btnEditPhone.setOnClickListener(this);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Log.d("nameeeeeeeeeeee", "" + usersInfo.getName());

        databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etEditName.setText(dataSnapshot.child("name").getValue() + "");
                etEditMail.setText(dataSnapshot.child("mail").getValue() + "");
                etEditPhone.setText(dataSnapshot.child("phone").getValue() + "");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePass: {
                changePassDialog = new Dialog(getActivity());
                changePassDialog.setContentView(R.layout.fragment_change_password);
                changePassDialog.findViewById(R.id.btnCancelChg).setOnClickListener(this);
                changePassDialog.findViewById(R.id.btnConfirmChg).setOnClickListener(this);
                changePassDialog.show();
                break;
            }
            case R.id.btnCancelChg: {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
                        changePassDialog.dismiss();
                    }
                });
                alert.create().show();
                break;
            }
            case R.id.btnConfirmChg: {
                etNewPass = (EditText) changePassDialog.findViewById(R.id.etNewPass);
                etCfNewPass = (EditText) changePassDialog.findViewById(R.id.etCfNewPass);
                String newPass = etNewPass.getText().toString();
                String cfNewPass = etCfNewPass.getText().toString();
                if (checkException(newPass, cfNewPass)) {
                    changePass(newPass);
                }
            }
            case R.id.btnEditName: {
                changeName();
                break;
            }
            case R.id.btnEditMail: {
                changeMail();
                break;
            }
            case R.id.btnEditPhone: {
                changePhone();
                break;
            }
        }
    }

    public boolean checkException(String newPass, String cfNewPass) {
        if (TextUtils.isEmpty(newPass)) {
            etNewPass.setError("Please enter your Password");
            return false;
        }
        if (!newPass.equals(cfNewPass)) {
            etCfNewPass.setError("Password must same");
            return false;
        }
        if (newPass.length() < 6) {
            etNewPass.setError("Your Password must be at least 6 character");
            return false;
        }
        return true;
    }

    public void changePass(String newPass) {
        userAuth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Change password successfully", Toast.LENGTH_LONG).show();
                changePassDialog.dismiss();
            }
        });
    }

    public void changeName() {
        if (!clickedBtnName) {
            oldName = etEditName.getText()+"";
            Log.d("Warning name", "" + oldName);
            clickedBtnName = true;
            etEditName.setEnabled(true);
            btnEditName.setBackgroundColor(Color.parseColor("#FFFF5F5F"));
        } else {
            clickedBtnName = false;
            etEditName.setEnabled(false);
            btnEditName.setBackgroundColor(Color.parseColor("#FFFCF0E4"));
            final String newName = etEditName.getText() + "";
            if (checkEditNameException(newName)) {
                databaseReference.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final long numberChild = dataSnapshot.getChildrenCount();
                        databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                countChild++;
                                Log.d("Warning number child", "" + numberChild);
                                final String getName = dataSnapshot.child("name").getValue() + "";
                                Log.d("Warning name", "" + getName);
                                if (getName.equals(newName)) {
                                    existedName = true;
                                }
                                Log.d("Warning exist name 1", "" + existedName);
                                if (countChild == numberChild) {
                                    if (!existedName) {
                                        databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("name").setValue(newName);
                                    } else {
                                        existedName = false;
                                        etEditName.setError("Username already existed");
                                        etEditName.setText(oldName);
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
    }
//
//    public void waitDialog() {
//        pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Please wait...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(false);
//        pDialog.show();
//    }


    private void changePhone() {
        if (!clickedBtnPhone) {
            clickedBtnPhone = true;
            etEditPhone.setEnabled(true);
            btnEditPhone.setBackgroundColor(Color.parseColor("#FFFF5F5F"));
        } else {
            clickedBtnPhone = false;
            etEditPhone.setEnabled(false);
            btnEditPhone.setBackgroundColor(Color.parseColor("#FFFCF0E4"));
            if (checkEditPhoneException(etEditPhone.getText() + "")) {
                //usersInfo.setPhone(etEditPhone.getText() + "");
                databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("phone").setValue(etEditPhone.getText() + "");
            }
        }
    }

    private void changeMail() {
        databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("mail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String oldMail = dataSnapshot.getValue()+"";
                Log.d("Waning old mail", oldMail);
                if (!clickedBtnMail) {
                    clickedBtnMail = true;
                    etEditMail.setEnabled(true);
                    btnEditMail.setBackgroundColor(Color.parseColor("#FFFF5F5F"));
                } else {
                    clickedBtnMail = false;
                    etEditMail.setEnabled(false);
                    final String newMail = etEditMail.getText() + "";
                    Log.d("Waning new mail", newMail);
                    btnEditMail.setBackgroundColor(Color.parseColor("#FFFCF0E4"));
                    if (!oldMail.equals(newMail)) {
                        if (checkEditMailException(newMail)) {
                            userAuth.getCurrentUser().updateEmail(newMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Change email success", Toast.LENGTH_SHORT).show();
                                        //usersInfo.setMail(newMail);
                                        databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("mail").setValue(newMail);
                                    } else {
                                        etEditMail.setError("Invalid email");
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private boolean checkEditNameException(String string) {
        if (TextUtils.isEmpty(string)) {
            etEditName.setError("Please enter your name");
            return false;
        }
        if (string.length() < 6 | string.length() > 16) {
            etEditName.setError("Username must be 6-16 character");
            return false;
        }
        return true;
    }

    private boolean checkEditMailException(String string) {
        if (TextUtils.isEmpty(string)) {
            etEditMail.setError("Please enter your email");
            return false;
        }
        return true;
    }

    private boolean checkEditPhoneException(String string) {
        if (TextUtils.isEmpty(string)) {
            etEditPhone.setError("Please enter your phone number");
            return false;
        }
        if (string.length() < 10 | string.length() > 11) {
            etEditPhone.setError("Invalid phone number");
            return false;
        }
        return true;
    }
}
