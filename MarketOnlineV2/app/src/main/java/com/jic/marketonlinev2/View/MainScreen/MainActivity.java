package com.jic.marketonlinev2.View.MainScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jic.marketonlinev2.Model.StoreInfo;
import com.jic.marketonlinev2.Model.UsersInfo;
import com.jic.marketonlinev2.R;
import com.jic.marketonlinev2.View.LoginAndRegister.LoginActivity;
import com.jic.marketonlinev2.View.MainScreen.Fragment.CreateStoreFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.HomeFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.InformationFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.MyOrderFragment;
import com.jic.marketonlinev2.View.MainScreen.Fragment.MyStoreFragment;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private Menu mMenu;
    private SearchView searchView;
    private TextView headName, headMail;
    //private UsersInfo user;
    private CircleImageView avatar;
    private static int RESULT_LOAD_IMAGE = 1;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;
    private FirebaseUser firebaseUser;
    private UserProfileChangeRequest userProfileChangeRequest;

    Bundle bundle = new Bundle();


    private StoreInfo storeInfo;
    private ListView lvStore;
    private ArrayList<StoreInfo> storeInfoArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Market Online");

        Intent intent = getIntent();
        //user = intent.getParcelableExtra("userInfo");
        //bundle.putParcelable("user", user);

        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragment.setArguments(bundle);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        View header = navigationView.getHeaderView(0);
        headName = (TextView) header.findViewById(R.id.nav_tv_username);
        headMail = (TextView) header.findViewById(R.id.nav_tv_email);
        avatar = (CircleImageView) header.findViewById(R.id.profile_image);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.child("USERS").child(userAuth.getCurrentUser().getUid());
                headName.setText(dataSnapshot.child("name").getValue() + "");
                headMail.setText(dataSnapshot.child("mail").getValue() + "");
                if (!(dataSnapshot.child("avatar").getValue() + "").equals("")) {
                    Picasso.with(MainActivity.this).load(dataSnapshot.child("avatar").getValue() + "").fit().into(avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImg = data.getData();
            Picasso.with(MainActivity.this).load(selectedImg).fit().into(avatar);
            uploadImg(selectedImg);
        }
    }

    private void uploadImg(Uri uriImg) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("ImageProfile").child(headName.getText() + "");
        storageReference.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = taskSnapshot.getDownloadUrl().toString();
                Uri downloadUrl = Uri.parse(url);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).child("avatar").setValue(downloadUrl + "");
//                userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build();
//                userAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
//        alert.setMessage("Are you want to exit");
//        alert.setPositiveButton("\t\t\t\t\t\tNo\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        alert.setNegativeButton("\t\t\t\t\t\tYes\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mAuth.signOut();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        alert.create().show();
//    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            HomeFragment fragment = new HomeFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            //fragment.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_information) {
            InformationFragment fragment = new InformationFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            //fragment.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_myShop) {
            // hide icon search
            //         mi.setVisible(false);
            databaseReference.child("USERS").child(userAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.d("Warning data snap", dataSnapshot.toString());
                    boolean existedShop = dataSnapshot.hasChild("shop");
                    if (existedShop) {
                        MyStoreFragment fragment = new MyStoreFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        //fragment.setArguments(bundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        CreateStoreFragment fragment = new CreateStoreFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        //fragment.setArguments(bundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (id == R.id.nav_myOrder) {
            // hide icon search
            //        mi.setVisible(false);

            MyOrderFragment fragment = new MyOrderFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Are you want to exit");
            alert.setPositiveButton("\t\t\t\t\t\tNo\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.setNegativeButton("\t\t\t\t\t\tYes\t\t\t\t\t\t", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            alert.create().show();
        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_copyright) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
