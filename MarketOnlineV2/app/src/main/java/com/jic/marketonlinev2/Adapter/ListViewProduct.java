package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.jic.marketonlinev2.Model.ProductOrder;
import com.jic.marketonlinev2.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jic on 10/11/2016.
 */

public class ListViewProduct extends ArrayAdapter<ProductInfo>{

    private Activity activity;
    private int resource;
    private List<ProductInfo> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    public ListViewProduct(Activity activity, int resource, List<ProductInfo> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
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

        TextView tvProductName = (TextView) view.findViewById(R.id.tvProductName);
        TextView tvProductPrice = (TextView) view.findViewById(R.id.tvProductPrice);
        ImageView ivProductName = (ImageView) view.findViewById(R.id.ivProductImg);

        final ProductInfo productInfo = this.objects.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String price = decimalFormat.format(Integer.parseInt(productInfo.getProPrice()));
        price = price.replaceAll(",", ".");
        price += " VNĐ";
        String name = productInfo.getProName();
        String urlImg = productInfo.getProImage();

        tvProductName.setText(name);
        tvProductPrice.setText(price);
        Picasso.with(getContext()).load(urlImg).fit().into(ivProductName);

        return view;
    }

}
