package com.jic.marketonlinev2.View.MainScreen.Fragment;



import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jic.marketonlinev2.Model.StoreInfo;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.R;


import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateStoreFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private String[] listAddress;
    private ArrayAdapter<String> adapterAddress;
    private Spinner spnAddress;
    private TextView tvTimeOpen, tvTimeClose;
    private EditText etStoreName, etStoreDes;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    UsersInfo usersInfo;

    public CreateStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_store, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();

        //usersInfo = getArguments().getParcelable("user");

        rootView.findViewById(R.id.btnTimeOpen).setOnClickListener(this);
        rootView.findViewById(R.id.btnTimeClose).setOnClickListener(this);
        rootView.findViewById(R.id.btnCreateShop).setOnClickListener(this);

        tvTimeOpen = (TextView) rootView.findViewById(R.id.tvTimeOpen);
        tvTimeClose = (TextView) rootView.findViewById(R.id.tvTimeClose);

        etStoreName = (EditText) rootView.findViewById(R.id.etStoreName);
        etStoreDes = (EditText) rootView.findViewById(R.id.etStoreDes);

        addSpinner();



        return rootView;
    }

    public void addSpinner() {
        spnAddress = (Spinner) rootView.findViewById(R.id.spnAddress);
        listAddress = getResources().getStringArray(R.array.arrAddress);
        adapterAddress = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listAddress);
        spnAddress.setAdapter(adapterAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTimeOpen: {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        DecimalFormat decimalFormat = new DecimalFormat("00");
                        tvTimeOpen.setText( decimalFormat.format(selectedHour) + " : " + decimalFormat.format(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            }
            case R.id.btnTimeClose: {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        DecimalFormat decimalFormat = new DecimalFormat("00");
                        tvTimeClose.setText( decimalFormat.format(selectedHour) + " : " + decimalFormat.format(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            }
            case R.id.btnCreateShop: {
                final String storeName = etStoreName.getText().toString();
                final String storeDes = etStoreDes.getText().toString();
                final String storeLocale = spnAddress.getSelectedItem().toString();
                final String storeTimeOpen = tvTimeOpen.getText().toString();
                final String storeTimeClose = tvTimeClose.getText().toString();
                if (checkCreateException(storeName, storeDes)) {
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeName").setValue(storeName);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeDes").setValue(storeDes);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeLocale").setValue(storeLocale);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeTimeOpen").setValue(storeTimeOpen);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeTimeClose").setValue(storeTimeClose);
                    databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop").child("storeImage").setValue("");
                    Toast.makeText(getActivity(), "Create Store Success !!!", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    MyStoreFragment fragmentMyStore = new MyStoreFragment();
                    transaction.replace(R.id.fragment_container, fragmentMyStore);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }
    }

    public boolean checkCreateException(String storeName, String storeDes) {
        if (TextUtils.isEmpty(storeName)) {
            etStoreName.setError("Please enter your Store Name");
            return false;
        }
        if (TextUtils.isEmpty(storeDes)) {
            etStoreDes.setError("Please enter your Store Describe");
            return false;
        }
        if (TextUtils.isEmpty(tvTimeOpen.getText().toString())) {
            tvTimeOpen.setError("Please choose Time Open");
            return false;
        }
        if (TextUtils.isEmpty(tvTimeClose.getText().toString())) {
            tvTimeClose.setError("Please choose Time Close");
            return false;
        }
        return true;
    }
}
