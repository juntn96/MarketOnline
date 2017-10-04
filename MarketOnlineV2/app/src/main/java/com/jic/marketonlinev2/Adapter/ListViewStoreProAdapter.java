package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Model.ProductInfo;
import com.jic.marketonlinev2.R;
import com.jic.marketonlinev2.View.MainScreen.Fragment.AddProductFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.UpdateProductFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.UpdateStoreFragment;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jic on 10/9/2016.
 */

public class ListViewStoreProAdapter extends ArrayAdapter<ProductInfo> {

    private Activity activity;
    private int resource;
    private List<ProductInfo> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private Fragment fragment;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    //private ImageButton btnDelete;

    public ListViewStoreProAdapter(Activity activity, int resource, List<ProductInfo> objects, Fragment fragment) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public ProductInfo getItem(int position) {
        return objects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(this.resource, null);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        TextView tvStoreProName = (TextView) view.findViewById(R.id.tvStoreProName);
        TextView tvStoreProPrice = (TextView) view.findViewById(R.id.tvStoreProPrice);

        ImageView ivStoreProImg = (ImageView) view.findViewById(R.id.ivStoreProImg);
        ImageButton btnDel = (ImageButton) view.findViewById(R.id.btnDeletePro);

        final ProductInfo productInfo = this.objects.get(position);


        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String price = decimalFormat.format(Integer.parseInt(productInfo.getProPrice()));
        price = price.replaceAll(",", ".");
        price += " VNĐ";
        String proName = productInfo.getProName();
        tvStoreProName.setText(proName);
        tvStoreProPrice.setText(price);
        Picasso.with(getContext()).load(productInfo.getProImage()).fit().into(ivStoreProImg);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(productInfo.getProName());
            }
        });
        return view;
    }

    public void delete(final String name) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are you want to delete Product ?");
        alert.setPositiveButton("\t\t\t\t\t\tNo\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setNegativeButton("\t\t\t\t\t\tYes\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product").child(name).removeValue();
                storageReference.child("PRODUCTS").child(userAuth.getCurrentUser().getUid()).child(name).delete();
            }
        });
        alert.create().show();
    }

}
