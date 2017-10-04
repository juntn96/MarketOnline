package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Model.ProductInfo;
import com.jic.marketonlinev2.Model.ProductOrder;
import com.jic.marketonlinev2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jic on 10/11/2016.
 */

public class ListViewSendOrder extends ArrayAdapter<ProductOrder> {

    private Activity activity;
    private int resource;
    private List<ProductOrder> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ListViewSendOrder(Activity activity, int resource, List<ProductOrder> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
    }

    @Nullable
    @Override
    public ProductOrder getItem(int position) {
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

        ProductOrder productOrder = this.objects.get(position);

        TextView tvProName = (TextView) view.findViewById(R.id.tvSendOrderProName);
        TextView tvProPrice = (TextView) view.findViewById(R.id.tvSendOrderProPrice);
        TextView tvProQuantity = (TextView) view.findViewById(R.id.tvSendOderProQuantity);

        tvProName.setText(productOrder.getProName());

        int price = Integer.parseInt(productOrder.getProPrice()) * productOrder.getQuantity();
        Log.d("warninggg", ">>>>> product get price  " + productOrder.getProPrice());
        Log.d("warninggg", ">>>>> productOrder.getQuantity()  " + productOrder.getQuantity());
        Log.d("warninggg", ">>>>> price  " + price);

        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String formatPrice = decimalFormat.format(price);
        formatPrice = formatPrice.replaceAll(",", ".");

        tvProPrice.setText(formatPrice+"");
        tvProQuantity.setText(productOrder.getQuantity()+"");

        return view;
    }
}
