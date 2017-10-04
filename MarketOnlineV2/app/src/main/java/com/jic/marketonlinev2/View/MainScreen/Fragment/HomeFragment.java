package com.jic.marketonlinev2.View.MainScreen.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jic.marketonlinev2.Adapter.ListViewStoreAdapter;
import com.jic.marketonlinev2.Model.StoreInfo;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.R;
import com.jic.marketonlinev2.View.MainScreen.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View rootView;

    private ListViewStoreAdapter listViewStoreAdapter;
    private ListView lvStore;
    private ArrayList<StoreInfo> storeInfoArrayList;


    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private SearchView searchView;
    boolean searching = false;

    private String[] listAddress;
    private ArrayList<String> arrayAddress = new ArrayList<>();
    private ArrayAdapter<String> adapterAddress;
    private Spinner spnAddress;

    //private UsersInfo usersInfo;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userAuth = FirebaseAuth.getInstance();

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //usersInfo = getArguments().getParcelable("user");

        setHasOptionsMenu(true);

        spnAddress = (Spinner) rootView.findViewById(R.id.spHomeLocale);
        listAddress = getResources().getStringArray(R.array.arrAddress);
        arrayAddress.add("Show All");
        for (int i = 0; i < listAddress.length; i++) {
            arrayAddress.add(listAddress[i]);
        }
        adapterAddress = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayAddress);
        spnAddress.setAdapter(adapterAddress);
        spnAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String locale = spnAddress.getSelectedItem().toString();
                if (!locale.equals("Show All")) {
                    Log.d("Locale 1", locale);
                    getStoreByLocale(locale);
                } else {
                    storeInfoArrayList = new ArrayList<StoreInfo>();
                    getAllStore();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storeInfoArrayList = new ArrayList<>();
        return rootView;
    }


    public void getStoreByLocale(final String locale) {
        storeInfoArrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(locale)) {
            databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.hasChild("shop")) {
                        final String address = dataSnapshot.child("shop").child("storeLocale").getValue()+"";
                        if (address.equals(locale)) {
                            final String name = dataSnapshot.child("shop").child("storeName").getValue()+"";
                            final String des = dataSnapshot.child("shop").child("storeDes").getValue()+"";
                            final String open = dataSnapshot.child("shop").child("storeTimeOpen").getValue()+"";
                            final String close = dataSnapshot.child("shop").child("storeTimeClose").getValue()+"";
                            final String getUsername = dataSnapshot.child("name").getValue()+"";
                            StoreInfo storeInfo = new StoreInfo(name, des, address, open, close, dataSnapshot.child("avatar").getValue() + "", getUsername);
                            storeInfoArrayList.add(storeInfo);
                            addStoreListView();
                            searching = true;
                        }
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
    }


    private void addStoreListView() {
        lvStore = (ListView) rootView.findViewById(R.id.lvStore);
        if (getActivity() != null) {
            listViewStoreAdapter = new ListViewStoreAdapter(getActivity(), R.layout.list_view_store, storeInfoArrayList);
        }
        lvStore.setAdapter(listViewStoreAdapter);
        if (searching) {
            listViewStoreAdapter.notifyDataSetChanged();
            searching = false;
        }
        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.tvShowStoreUserName);
                //Toast.makeText(getActivity(), textView.getText()+"", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("name", textView.getText()+"");

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                ViewStoreFragment viewStoreFragment = new ViewStoreFragment();

                transaction.replace(R.id.fragment_container, viewStoreFragment);
                transaction.addToBackStack(null);
                viewStoreFragment.setArguments(bundle);
                transaction.commit();

            }
        });
    }

    private void getAllStore() {
        databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChild("shop")) {
                    final String name = dataSnapshot.child("shop").child("storeName").getValue()+"";
                    final String des = dataSnapshot.child("shop").child("storeDes").getValue()+"";
                    final String address = dataSnapshot.child("shop").child("storeLocale").getValue()+"";
                    final String open = dataSnapshot.child("shop").child("storeTimeOpen").getValue()+"";
                    final String close = dataSnapshot.child("shop").child("storeTimeClose").getValue()+"";
                    final String getUsername = dataSnapshot.child("name").getValue()+"";
                    StoreInfo storeInfo = new StoreInfo(name, des, address, open, close, dataSnapshot.child("avatar").getValue() + "", getUsername);
                    storeInfoArrayList.add(storeInfo);
                    addStoreListView();
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        View view = (View) menu.findItem(R.id.action_search).getActionView();
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) view.findViewById(R.id.action_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // get text of search view

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchStoreName = searchView.getQuery().toString();
                getStoreByName(searchStoreName);
                //Toast.makeText(getActivity(), searchView.getQuery(), Toast.LENGTH_LONG).show();
                if (newText.equals("")) {
                    getAllStore();
                }
                return false;
            }
        });
    }

    public void getStoreByName(final String searchStoreName) {
        storeInfoArrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(searchStoreName)) {
            databaseReference.child("USERS").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.hasChild("shop")) {
                        //String getStoreName = dataSnapshot.child("shop").child("storeName").getValue()+"";
                        final String name = dataSnapshot.child("shop").child("storeName").getValue()+"";
                        if (name.toLowerCase().contains(searchStoreName.toLowerCase())) {
                            final String des = dataSnapshot.child("shop").child("storeDes").getValue()+"";
                            final String address = dataSnapshot.child("shop").child("storeLocale").getValue()+"";
                            final String open = dataSnapshot.child("shop").child("storeTimeOpen").getValue()+"";
                            final String close = dataSnapshot.child("shop").child("storeTimeClose").getValue()+"";
                            final String username = dataSnapshot.child("name").getValue()+"";
                            StoreInfo storeInfo = new StoreInfo(name, des, address, open, close, dataSnapshot.child("avatar").getValue() + "", username);
                            storeInfoArrayList.add(storeInfo);
                            searching = true;
                            addStoreListView();
                        }
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
    }

}
