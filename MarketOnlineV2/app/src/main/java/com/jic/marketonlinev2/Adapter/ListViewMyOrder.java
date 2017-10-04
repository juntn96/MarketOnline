package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Model.GetOrder;
import com.jic.marketonlinev2.R;

import java.util.List;

/**
 * Created by Jic on 10/13/2016.
 */

public class ListViewMyOrder extends ArrayAdapter<GetOrder> {

    private Activity activity;
    private int resource;
    private List<GetOrder> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ListViewMyOrder(Activity activity, int resource, List<GetOrder> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
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

        final TextView tvStoreName = (TextView) view.findViewById(R.id.tvMyOrderStoreName);
        final TextView tvStatus = (TextView) view.findViewById(R.id.tvMyOrderStatus);
        final TextView tvMyOrder = (TextView) view.findViewById(R.id.tvMyOrder);
        final ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDeleteMyOrder);


        final GetOrder getOrder = this.objects.get(position);

        databaseReference.child("USERS").child(getOrder.getStoreID()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvStoreName.setText(dataSnapshot.getValue()+"");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String status = getOrder.getOrderStatus();
        if (status.equals("Waiting")) {
            tvStatus.setTextColor(Color.YELLOW);
        } else if (status.equals("Accept")) {
            tvStatus.setTextColor(Color.GREEN);
        } else if (status.equals("Decline")) {
            tvStatus.setTextColor(Color.RED);
        }
        tvStatus.setText(status);

        tvMyOrder.setText(getOrder.getBuyderOrder());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("USER_ORDERS").child(getOrder.getKey()).removeValue();
                objects.remove(getOrder);
            }
        });

        return view;
    }
}
