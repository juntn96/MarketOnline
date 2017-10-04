package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jic.marketonlinev2.Adapter.ListViewSendOrder;
import com.jic.marketonlinev2.Model.ProductOrder;
import com.jic.marketonlinev2.Model.SendOrder;
import com.jic.marketonlinev2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewOderBuyerFragment extends Fragment implements View.OnClickListener{


    private View rootView;
    private ArrayList<ProductOrder> orderArrayList = new ArrayList<>();
    private String username;

    private ListViewSendOrder productAdapter;
    private ListView lvProduct;

    private ArrayList<ProductOrder> productArr = new ArrayList<>();
    private ArrayList<ProductOrder> sendOrder;
    private ProductOrder order = new ProductOrder();
    private EditText etAddress, etMsg;

    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Dialog sendOrderDialog;

    public ViewOderBuyerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_send_oder_view, container, false);

        orderArrayList = getArguments().getParcelableArrayList("product");
        username = getArguments().getString("username");

        Log.d("username", username);

        rootView.findViewById(R.id.btnBackViewOrder).setOnClickListener(this);
        rootView.findViewById(R.id.btnSendOrder).setOnClickListener(this);

        etAddress = (EditText) rootView.findViewById(R.id.etBuyerAddress);
        etMsg = (EditText) rootView.findViewById(R.id.etBuyerMessage);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getProduct();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendOrder: {
                sendOrderDialog = new Dialog(getActivity());
                sendOrderDialog.setContentView(R.layout.dialog_send_order);
                sendOrderDialog.findViewById(R.id.btnConfirmSend).setOnClickListener(this);
                int totalMoney = 0;
                for (int i = 0; i < sendOrder.size(); i++) {
                    totalMoney += Integer.parseInt(sendOrder.get(i).getProPrice()) * sendOrder.get(i).getQuantity();
                }
                DecimalFormat decimalFormat = new DecimalFormat("###,###");
                String formatPrice = decimalFormat.format(Integer.parseInt(totalMoney+""));
                formatPrice = formatPrice.replaceAll(",", ".");
                TextView tvTotal = (TextView) sendOrderDialog.findViewById(R.id.tvTotalMoney);
                tvTotal.setText(formatPrice + " VNĐ");
                sendOrderDialog.getWindow().setLayout(700, 600);
                sendOrderDialog.show();
                break;
            }
            case R.id.btnBackViewOrder: {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("product", productArr);
                bundle.putString("name", username);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                ViewStoreFragment viewStoreFragment = new ViewStoreFragment();
                transaction.replace(R.id.fragment_container, viewStoreFragment);
                transaction.addToBackStack(null);
                viewStoreFragment.setArguments(bundle);
                transaction.commit();
                break;
            }
            case R.id.btnConfirmSend: {
                String address = etAddress.getText()+"";
                String mes = etMsg.getText()+"";
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getActivity(), "Please enter Your Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                doSendOrder();
                Bundle bundle = new Bundle();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                bundle.putString("name", username);
                ViewStoreFragment viewStoreFragment = new ViewStoreFragment();
                transaction.replace(R.id.fragment_container, viewStoreFragment);
                viewStoreFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
        }
    }

    private void doSendOrder() {
        databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("name").getValue().equals(username)) {
                    final String getStoreID = dataSnapshot.getKey();
                    final StringBuilder buyerOrder = new StringBuilder();
                    final String buyerAddress = etAddress.getText()+"";
                    final String buyerMes = etMsg.getText()+"";
                    final String buyerID = userAuth.getCurrentUser().getUid();
                    for (int i = 0; i < sendOrder.size(); i++) {
                           buyerOrder.append(sendOrder.get(i).getProName() + " - " + sendOrder.get(i).getQuantity() + "\n");
                    }
                    SendOrder sendOrder = new SendOrder(buyerAddress, buyerMes, buyerOrder.toString(), buyerID, "Waiting", getStoreID);
                    String key = databaseReference.child("STORE_ORDERS").push().getKey();
                    databaseReference.child("STORE_ORDERS").child(key).setValue(sendOrder);
                    databaseReference.child("USER_ORDERS").child(key).setValue(sendOrder);
                    Toast.makeText(getActivity(), "Send Order success. Please wait Store respone", Toast.LENGTH_SHORT).show();
                    sendOrderDialog.dismiss();
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
    }

    public void getProduct() {
        int quantity;
        HashMap<String , ProductOrder> productOrderHashMap = new HashMap<>();
        if (orderArrayList != null) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                quantity = orderArrayList.get(i).getQuantity();
                for (int j = i+1; j < orderArrayList.size(); j++) {
                    if (orderArrayList.get(i).getProName().equals(orderArrayList.get(j).getProName())) {
                        quantity += orderArrayList.get(j).getQuantity();
                        orderArrayList.remove(j);
                        j = i;
                    }
                }
                orderArrayList.get(i).setQuantity(quantity);
                order = new ProductOrder(orderArrayList.get(i).getProName(), orderArrayList.get(i).getProPrice()+"", quantity);
                productArr.add(order);
            }
            for (int i = 0; i < productArr.size(); i++) {
                productOrderHashMap.put(productArr.get(i).getProName(), productArr.get(i));
            }
            sendOrder = new ArrayList<>();
            for (String key : productOrderHashMap.keySet()) {
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>  " + key + ">>>>  " + productOrderHashMap.get(key));
                sendOrder.add(productOrderHashMap.get(key));
            }
            addToListView();
        }

    }


    public void addToListView() {
        lvProduct = (ListView) rootView.findViewById(R.id.lvViewStoreOrder);
        if (getActivity() != null) {
            productAdapter = new ListViewSendOrder(getActivity(), R.layout.list_view_send_order, sendOrder);
        }
        lvProduct.setAdapter(productAdapter);
    }
}
