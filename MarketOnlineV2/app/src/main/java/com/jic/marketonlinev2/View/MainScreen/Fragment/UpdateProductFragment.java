package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jic.marketonlinev2.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProductFragment extends Fragment implements View.OnClickListener {


    private View rootView;

    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static int RESULT_LOAD_IMAGE = 1;

    private EditText etUpdateProName, etUpdateProPrice;
    private ImageView ivUpdateImgPro;

    private String proName;

    private Uri selectImage;


    public UpdateProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_update_product, container, false);

        proName = getArguments().getString("proName");

        etUpdateProName = (EditText) rootView.findViewById(R.id.etUpdateProName);
        etUpdateProPrice = (EditText) rootView.findViewById(R.id.etUpdateProPrice);
        ivUpdateImgPro = (ImageView) rootView.findViewById(R.id.ivUpdateImageProduct);
        rootView.findViewById(R.id.btnUpdatePro).setOnClickListener(this);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etUpdateProName.setText(proName);
                etUpdateProPrice.setText(dataSnapshot.child(proName).child("proPrice").getValue() + "");
                Picasso.with(getContext()).load(dataSnapshot.child(proName).child("proImage").getValue().toString()).fit().into(ivUpdateImgPro);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(">>>>>> name:", proName);
        rootView.findViewById(R.id.btnBackUpdatePro).setOnClickListener(this);
        rootView.findViewById(R.id.btnBackUpdatePro).setOnClickListener(this);


        ivUpdateImgPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectImage = data.getData();
            Picasso.with(getActivity()).load(selectImage).fit().into(ivUpdateImgPro);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackUpdatePro: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MyStoreFragment myStoreFragment = new MyStoreFragment();
                transaction.replace(R.id.fragment_container, myStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case R.id.btnUpdatePro: {
                String newName = etUpdateProName.getText() + "";
                String newPrice = etUpdateProPrice.getText() + "";
                if (checkUpdateException(newName, newPrice)) {
                    if (!newName.equals(proName)) {
                        if (selectImage == null) {
                            firebaseStorage.getReference().child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(proName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    selectImage = uri;
                                }
                            });
                        }
                        uploadImgNewName(newName);
                        databaseReference.child(newName).child("proPrice").setValue(newPrice);
                        databaseReference.child(proName).removeValue();
                    } else {
                        if (selectImage != null) {
                            upLoad();
                        }
                        databaseReference.child(proName).child("proPrice").setValue(newPrice);
                    }

                    //uploadImg(newName);
                }
                break;
            }
        }
    }

    public boolean checkUpdateException(String name, String price) {
        if (TextUtils.isEmpty(name)) {
            etUpdateProName.setError("Please enter Product's Name");
            return false;
        }
        if (TextUtils.isEmpty(price)) {
            etUpdateProPrice.setError("Please enter Product's Price");
            return false;
        }
        return true;
    }

    private void uploadImgNewName(final String newName) {
        storageReference = firebaseStorage.getReference().child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(newName);
        if (selectImage != null) {
            storageReference.putFile(selectImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    databaseReference.child(newName).child("proImage").setValue(taskSnapshot.getDownloadUrl());
                    firebaseStorage.getReference().child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(proName).delete();
                }
            });
        }
    }

    private void upLoad() {
        storageReference = firebaseStorage.getReference().child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(proName);
        storageReference.putFile(selectImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                databaseReference.child(proName).child("proImage").setValue(taskSnapshot.getDownloadUrl());
            }
        });

    }

}
