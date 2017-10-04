package com.jic.marketonlinev2.View.MainScreen.Fragment;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.jic.marketonlinev2.R;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateStoreFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private EditText etUpdateName, etUpdateDes;
    private TextView tvUdateTimeOpen, tvUpdateTimeClose;

    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;

    private String[] listAddress;
    private ArrayAdapter<String> adapterAddress;
    private Spinner spnAddress;


    public UpdateStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_update_store, container, false);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("USERS").child(userAuth.getCurrentUser().getUid()).child("shop");

        rootView.findViewById(R.id.btnUpdateOpen).setOnClickListener(this);
        rootView.findViewById(R.id.btnUpdateClose).setOnClickListener(this);
        rootView.findViewById(R.id.btnBackUpdateStore).setOnClickListener(this);
        rootView.findViewById(R.id.btnUpdateStore).setOnClickListener(this);
        rootView.findViewById(R.id.btnUpdateOpen).setOnClickListener(this);

        etUpdateName = (EditText) rootView.findViewById(R.id.etUpdateStoreName);
        etUpdateDes = (EditText) rootView.findViewById(R.id.etUpdateStoreDes);



        tvUdateTimeOpen = (TextView) rootView.findViewById(R.id.tvUpdateTimeOpen);
        tvUpdateTimeClose = (TextView) rootView.findViewById(R.id.tvUpdateTimeClose);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etUpdateName.setText(dataSnapshot.child("storeName").getValue()+"");
                etUpdateDes.setText(dataSnapshot.child("storeDes").getValue()+"");
                tvUdateTimeOpen.setText(dataSnapshot.child("storeTimeOpen").getValue()+"");
                tvUpdateTimeClose.setText(dataSnapshot.child("storeTimeClose").getValue()+"");
                addSpinner();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public void addSpinner() {
        spnAddress = (Spinner) rootView.findViewById(R.id.spnUpdateAddress);
        listAddress = getResources().getStringArray(R.array.arrAddress);
        adapterAddress = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listAddress);
        spnAddress.setAdapter(adapterAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateStore: {
                String updateName = etUpdateName.getText()+"";
                String updateDes = etUpdateDes.getText()+"";
                String updateLocale = spnAddress.getSelectedItem()+"";
                String updateTimeOpen = tvUdateTimeOpen.getText()+"";
                String updateTimeClose = tvUpdateTimeClose.getText()+"";
                if (checkUpdateException(updateName, updateDes)) {
                    updateStore(updateName, updateDes, updateLocale, updateTimeOpen, updateTimeClose);
                }
                break;
            }
            case R.id.btnBackUpdateStore: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MyStoreFragment myStoreFragment = new MyStoreFragment();
                transaction.replace(R.id.fragment_container, myStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case R.id.btnUpdateOpen: {
                timeOpen();
                break;
            }
            case R.id.btnUpdateClose: {
                timeClose();
                break;
            }
        }
    }

    public void timeOpen() {
        Toast.makeText(getActivity(), "Gw", Toast.LENGTH_LONG).show();;
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                DecimalFormat decimalFormat = new DecimalFormat("00");
                tvUdateTimeOpen.setText( decimalFormat.format(selectedHour) + " : " + decimalFormat.format(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void timeClose() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                DecimalFormat decimalFormat = new DecimalFormat("00");
                tvUpdateTimeClose.setText( decimalFormat.format(selectedHour) + " : " + decimalFormat.format(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public boolean checkUpdateException(String name, String des) {
        if (TextUtils.isEmpty(name)) {
            etUpdateName.setError("Please enter Store's Name");
            return false;
        }
        if (TextUtils.isEmpty(des)) {
            etUpdateDes.setError("Please enter Store's Describe");
            return false;
        }
        return true;
    }

    public void updateStore(String updateName, String updateDes, String updateLocale, String updateTimeOpen, String updateTimeClose) {
        databaseReference.child("storeName").setValue(updateName);
        databaseReference.child("storeDes").setValue(updateDes);
        databaseReference.child("storeLocale").setValue(updateLocale);
        databaseReference.child("storeTimeOpen").setValue(updateTimeOpen);
        databaseReference.child("storeTimeClose").setValue(updateTimeClose);
    }

}
