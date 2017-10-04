package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Adapter.ListViewListOrderStore;
import com.jic.marketonlinev2.Model.GetOrder;
import com.jic.marketonlinev2.Model.SendOrder;
import com.jic.marketonlinev2.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckOrderFragment extends Fragment {

    private View rootView;

    private ListViewListOrderStore listViewListOrderStore;
    private ListView lvListOrder;
    private ArrayList<GetOrder> sendOrderArrayList;

    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String buyerName;

    public CheckOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_check_order, container, false);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getOrder();

        return rootView;
    }

    private void getOrder() {
        sendOrderArrayList = new ArrayList<>();
        databaseReference.child("STORE_ORDERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("storeID").getValue().equals(userAuth.getCurrentUser().getUid())) {
                    Log.d("okkkkkkkk" , "gettttttttt");
                    final String storeID = dataSnapshot.child("storeID").getValue()+"";
                    final String buyerID = dataSnapshot.child("buyerID").getValue()+"";
                    final String buyerMes = dataSnapshot.child("buyerMes").getValue()+"";
                    final String buyerOrder = dataSnapshot.child("buyderOrder").getValue()+"";
                    final String buyerAddress = dataSnapshot.child("buyerAddress").getValue()+"";
                    final String key = dataSnapshot.getKey();
                    GetOrder getOrder = new GetOrder(buyerAddress, buyerMes, buyerOrder, buyerID, "waiting", storeID, key);
                    sendOrderArrayList.add(getOrder);
                    addToListView();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                addToListView();
                lvListOrder.deferNotifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addToListView() {
        lvListOrder = (ListView) rootView.findViewById(R.id.lvListOrder);
        if (getActivity() != null) {
            listViewListOrderStore = new ListViewListOrderStore(getActivity(), R.layout.list_view_check_order, sendOrderArrayList);
        }
        lvListOrder.setAdapter(listViewListOrderStore);
        lvListOrder.deferNotifyDataSetChanged();
    }
}
