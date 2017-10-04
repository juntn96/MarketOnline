package com.jic.marketonlinev2.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jic.marketonlinev2.Model.StoreInfo;
import com.jic.marketonlinev2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jic on 10/7/2016.
 */

public class ListViewStoreAdapter extends ArrayAdapter<StoreInfo> {
    private Activity activity;
    private int resource;
    private List<StoreInfo> objects;
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuth;

    public ListViewStoreAdapter(Activity activity, int resource, List<StoreInfo> objects) {
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
        TextView tvName = (TextView) view.findViewById(R.id.tvShowStoreName);
        TextView tvDes = (TextView) view.findViewById(R.id.tvShowStoreDes);
        TextView tvTime = (TextView) view.findViewById(R.id.tvShowStoreTime);
        TextView tvLocalse = (TextView) view.findViewById(R.id.tvShowStoreLocale);
        ImageView ivShopAvatar = (ImageView) view.findViewById(R.id.imageStore);
        TextView tvUsername = (TextView) view.findViewById(R.id.tvShowStoreUserName);

        RatingBar rbStore = (RatingBar) view.findViewById(R.id.showRatingBar);

        StoreInfo storeInfo = this.objects.get(position);

        tvName.setText(storeInfo.getStoreName());
        tvDes.setText(storeInfo.getStoreDes());
        tvTime.setText(storeInfo.getStoreTimeOpen() + " - " + storeInfo.getStoreTimeClose());
        tvLocalse.setText(storeInfo.getStoreLocale());
        tvUsername.setText(storeInfo.getStoreUserName());
        if (!storeInfo.getStoreImage().equals("")) {
            Picasso.with(getContext()).load(storeInfo.getStoreImage()).fit().centerCrop().into(ivShopAvatar);
        }
        return view;
    }
}
