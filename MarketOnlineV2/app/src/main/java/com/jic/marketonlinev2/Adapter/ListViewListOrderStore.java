package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Model.GetOrder;
import com.jic.marketonlinev2.Model.SendOrder;
import com.jic.marketonlinev2.R;

import java.util.List;

/**
 * Created by Jic on 10/12/2016.
 */

public class ListViewListOrderStore extends ArrayAdapter<GetOrder>{

    private Activity activity;
    private int resource;
    private List<GetOrder> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ListViewListOrderStore(Activity activity, int resource, List<GetOrder> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
    }

    @Nullable
    @Override
    public GetOrder getItem(int position) {
        return objects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(this.resource, null);

        final GetOrder getOrder = this.objects.get(position);

        final TextView tvBuyerName = (TextView) view.findViewById(R.id.tvStoreBuyerName);
        final TextView tvBuyerAddress = (TextView) view.findViewById(R.id.tvStoreBuyerAddress);
        final TextView tvBuyerMes = (TextView) view.findViewById(R.id.tvStoreBuyerMes);
        final TextView tvBuyerOrder = (TextView) view.findViewById(R.id.tvStoreBuyerOrder);
        Button btnAccept = (Button) view.findViewById(R.id.btnStoreAccept);
        Button btnDecline = (Button) view.findViewById(R.id.btnStoreDecline);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        databaseReference.child("USERS").child(getOrder.getBuyerID()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvBuyerName.setText(dataSnapshot.getValue()+"");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvBuyerAddress.setText(getOrder.getBuyerAddress());
        tvBuyerMes.setText(getOrder.getBuyerMes());
        tvBuyerOrder.setText(getOrder.getBuyderOrder());

        //Toast.makeText(getContext(), getOrder.getKey(), Toast.LENGTH_SHORT).show();

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("STORE_ORDERS").child(getOrder.getKey()).removeValue();
                getOrder.setOrderStatus("Accept");
                databaseReference.child("USER_ORDERS").child(getOrder.getKey()).setValue(getOrder);
                objects.remove(getOrder);
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("STORE_ORDERS").child(getOrder.getKey()).removeValue();
                getOrder.setOrderStatus("Decline");
                databaseReference.child("USER_ORDERS").child(getOrder.getKey()).setValue(getOrder);
                objects.remove(getOrder);
            }
        });

        return view;
    }

}
