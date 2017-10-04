package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.jic.marketonlinev2.Adapter.ListViewProduct;
import com.jic.marketonlinev2.Adapter.ListViewStoreProAdapter;
import com.jic.marketonlinev2.Model.ProductInfo;
import com.jic.marketonlinev2.Model.ProductOrder;
import com.jic.marketonlinev2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewStoreFragment extends Fragment implements View.OnClickListener {


    private View rootView;

    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;


    private ListViewProduct productAdapter;
    private ListView lvProduct;
    private ArrayList<ProductInfo> productArray;

    private TextView tvViewStoreName;

    private String username;

    private Bundle bundle = new Bundle();

    private ArrayList<ProductOrder> orderArrayList = new ArrayList<>();

    public ViewStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_store, container, false);

        username = getArguments().getString("name");

        if (getArguments().getParcelableArrayList("product") != null) {
            orderArrayList = getArguments().getParcelableArrayList("product");
            for (int i = 0; i < orderArrayList.size(); i++) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>   " + orderArrayList.get(i));
            }
        }


        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        tvViewStoreName = (TextView) rootView.findViewWithTag(R.id.tvViewStoreName);

        //Log.d("user name", username);

        rootView.findViewById(R.id.btnViewBasket).setOnClickListener(this);

        databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("name").getValue().equals(username)) {
                    tvViewStoreName = (TextView) rootView.findViewById(R.id.tvViewStoreName);
                    String getStoreName = dataSnapshot.child("shop").child("storeName").getValue() + "";
                    tvViewStoreName.setText(getStoreName);
                    getProduct(dataSnapshot.getKey());
                }
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

        return rootView;
    }

    public void getProduct(final String uid) {
        Log.d("warning uid", uid);
        productArray = new ArrayList<>();
        databaseReference.child("USERS").child(uid).child("shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("waning has child", dataSnapshot.hasChild("product")+"");
                if (dataSnapshot.hasChild("product")) {
                    databaseReference.child("USERS").child(uid).child("shop").child("product").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            Log.d("waning product", dataSnapshot.getKey());
                            final String proName = dataSnapshot.getKey();
                            databaseReference.child("USERS").child(uid).child("shop").child("product").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String proPrice = dataSnapshot.child("proPrice").getValue() + "";
                                    final String proImage = dataSnapshot.child("proImage").getValue() + "";
                                    ProductInfo productInfo = new ProductInfo(proName, proPrice, proImage);
                                    productArray.add(productInfo);
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
        lvProduct = (ListView) rootView.findViewById(R.id.lvViewStore);
        if (getActivity() != null) {
            productAdapter = new ListViewProduct(getActivity(), R.layout.list_view_store_product, productArray);
        }
        lvProduct.setAdapter(productAdapter);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final ImageView ivAddBasket = (ImageView) view.findViewById(R.id.ivAddBasket);
                final TextView tvProName = (TextView) view.findViewById(R.id.tvProductName);
                final TextView tvProPrice = (TextView) view.findViewById(R.id.tvProductPrice);
                final String price = tvProPrice.getText().toString();
                final String name = tvProName.getText().toString();
                Toast.makeText(getContext(), "Selected: " + tvProName.getText()+"", Toast.LENGTH_SHORT).show();
                ProductOrder productOrder = new ProductOrder(name, formatPrice(price), 1);
                orderArrayList.add(productOrder);
//                ivAddBasket.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewBasket: {
                if (!orderArrayList.isEmpty()) {
                    bundle.putParcelableArrayList("product", orderArrayList);
                    bundle.putString("username", username);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    ViewOderBuyerFragment viewOderBuyerFragment = new ViewOderBuyerFragment();
                    transaction.replace(R.id.fragment_container, viewOderBuyerFragment);
                    transaction.addToBackStack(null);
                    viewOderBuyerFragment.setArguments(bundle);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Please Select Something", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public String formatPrice(String price) {
        price = price.substring(0, price.lastIndexOf(" "));
        StringBuilder newPrice = new StringBuilder("");
        StringTokenizer stringTokenizer = new StringTokenizer(price, ".");
        while (stringTokenizer.hasMoreTokens()) {
            newPrice.append(stringTokenizer.nextToken());
        }
        //price = price.substring(0, price.lastIndexOf(".")) + price.substring(price.indexOf(".") + 1, price.length());
        Log.d("price", newPrice+"");
        return newPrice+"";
    }

}
