package com.jic.marketonlinev2.View.MainScreen.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jic.marketonlinev2.Model.ProductInfo;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Jic on 10/9/2016.
 */

public class AddProductFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageView ivProImage;
    private static int RESULT_LOAD_IMAGE = 1;
    private EditText etProName, etProPrice;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth userAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private UserProfileChangeRequest userProfileChangeRequest;
    private UsersInfo usersInfo;

    private String uriImagePro;
    private String proName, proPrice;

    private Bundle bundle = new Bundle();

    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_product, container, false);

        //usersInfo = getArguments().getParcelable("user");

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        rootView.findViewById(R.id.btnBack).setOnClickListener(this);
        rootView.findViewById(R.id.btnCreateProduct).setOnClickListener(this);
        etProName = (EditText) rootView.findViewById(R.id.etProName);
        etProPrice = (EditText) rootView.findViewById(R.id.etProPrice);

        ivProImage = (ImageView) rootView.findViewById(R.id.ivCreateImageProduct);
        ivProImage.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateProduct: {
                proName = etProName.getText()+"";
                proPrice = etProPrice.getText()+"";
                if (checkCreateExeption()) {
                    uploadImg();
                }
                break;
            }
            case R.id.btnBack: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MyStoreFragment myStoreFragment = new MyStoreFragment();
                transaction.replace(R.id.fragment_container, myStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case R.id.ivCreateImageProduct: {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImg = data.getData();
            Picasso.with(getActivity()).load(selectedImg).fit().into(ivProImage);
        }
    }

    private void uploadImg() {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ivProImage.setDrawingCacheEnabled(true);
        ivProImage.buildDrawingCache();
        Bitmap bitmap = ivProImage.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        storageReference =  firebaseStorage.getReference().child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(proName);
        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Log.d("WARNING PRODUCT IMAGE", ""+taskSnapshot.getDownloadUrl());
                uriImagePro = taskSnapshot.getDownloadUrl()+"";
                if (TextUtils.isEmpty(uriImagePro)) {
                    Toast.makeText(getActivity(), "Please set Product's Image", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("WARNING PRODUCT IMAGE", uriImagePro);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product").child(proName).child("proPrice").setValue(proPrice);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product").child(proName).child("proImage").setValue(uriImagePro);
                    Toast.makeText(getActivity(), "Add new Product success", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean checkCreateExeption() {
        if (TextUtils.isEmpty(proName)) {
            etProName.setError("Please enter Product's Name");
            return false;
        }
        if (TextUtils.isEmpty(proPrice)) {
            etProPrice.setError("Please enter Product's Price");
            return false;
        }
        return true;
    }
}
