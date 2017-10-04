package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
import com.jic.marketonlinev2.Adapter.ListViewStoreProAdapter;
import com.jic.marketonlinev2.Model.ProductInfo;
import com.jic.marketonlinev2.R;


import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyStoreFragment extends Fragment implements View.OnClickListener {


    private View rootView;

    private ListViewStoreProAdapter storeProAdapter;
    private ListView lvStorePro;
    private ArrayList<ProductInfo> proInfoArray;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;



    private String proName;

    public MyStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_store, container, false);

        //usersInfo = getArguments().getParcelable("user");
        //bundle.putParcelable("user", usersInfo);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        rootView.findViewById(R.id.btnAddProduct).setOnClickListener(this);
        rootView.findViewById(R.id.btnUpdateMyStore).setOnClickListener(this);
        rootView.findViewById(R.id.btnCheckOrder).setOnClickListener(this);

        databaseReference.child("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getProduct();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public void getProduct() {
        proInfoArray = new ArrayList<>();
        databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("waning has child", dataSnapshot.hasChild("product")+"");
                if (dataSnapshot.hasChild("product")) {
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            //Log.d("waning product", dataSnapshot.getKey());
                            final String proName = dataSnapshot.getKey();
                            databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("product").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String proPrice = dataSnapshot.child("proPrice").getValue()+"";
                                    final String proImage = dataSnapshot.child("proImage").getValue()+"";
                                    ProductInfo productInfo = new ProductInfo(proName, proPrice, proImage);
                                    proInfoArray.add(productInfo);
                                    addProToListView();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addProToListView() {
        lvStorePro = (ListView) rootView.findViewById(R.id.lvMyStorePro);

        if (getActivity() != null) {
            storeProAdapter = new ListViewStoreProAdapter(getActivity(), R.layout.list_view_product, proInfoArray, MyStoreFragment.this);
        }
        lvStorePro.setAdapter(storeProAdapter);
        //storeProAdapter.notifyDataSetChanged();
//        lvStorePro.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tvProName = (TextView) view.findViewById(R.id.tvStoreProName);
//                final Bundle bundle = new Bundle();
//                bundle.putString("proName", tvProName.getText()+"");
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                UpdateProductFragment updateProductFragment = new UpdateProductFragment();
//                transaction.replace(R.id.fragment_container, updateProductFragment);
//                updateProductFragment.setArguments(bundle);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddProduct: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AddProductFragment addProductFragment = new AddProductFragment();
                transaction.replace(R.id.fragment_container, addProductFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case R.id.btnUpdateMyStore: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                UpdateStoreFragment updateStoreFragment = new UpdateStoreFragment();
                transaction.replace(R.id.fragment_container, updateStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case R.id.btnCheckOrder: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                CheckOrderFragment checkOrderFragment = new CheckOrderFragment();
                transaction.replace(R.id.fragment_container, checkOrderFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
        }
    }
}
