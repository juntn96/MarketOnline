package com.jic.marketonlinev2.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jic on 10/3/2016.
 */

public class StoreInfo implements Parcelable {
    private String storeName;
    private String storeDes;
    private String storeLocale;
    private String storeTimeOpen;
    private String storeTimeClose;
    private String storeImage;
    private String storeUserName;

    public StoreInfo() {
    }

    public StoreInfo(String storeName, String storeDes, String storeLocale, String storeTimeOpen, String storeTimeClose, String storeImage, String storeUserName) {
        this.storeName = storeName;
        this.storeDes = storeDes;
        this.storeLocale = storeLocale;
        this.storeTimeOpen = storeTimeOpen;
        this.storeTimeClose = storeTimeClose;
        this.storeImage = storeImage;
        this.storeUserName = storeUserName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDes() {
        return storeDes;
    }

    public void setStoreDes(String storeDes) {
        this.storeDes = storeDes;
    }

    public String getStoreLocale() {
        return storeLocale;
    }

    public void setStoreLocale(String storeLocale) {
        this.storeLocale = storeLocale;
    }

    public String getStoreTimeOpen() {
        return storeTimeOpen;
    }

    public void setStoreTimeOpen(String storeTimeOpen) {
        this.storeTimeOpen = storeTimeOpen;
    }

    public String getStoreTimeClose() {
        return storeTimeClose;
    }

    public void setStoreTimeClose(String storeTimeClose) {
        this.storeTimeClose = storeTimeClose;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getStoreUserName() {
        return storeUserName;
    }

    public void setStoreUserName(String storeUserName) {
        this.storeUserName = storeUserName;
    }

    public static Creator<StoreInfo> getCREATOR() {
        return CREATOR;
    }

    protected StoreInfo(Parcel in) {
        storeName = in.readString();
        storeDes = in.readString();
        storeLocale = in.readString();
        storeTimeOpen = in.readString();
        storeTimeClose = in.readString();
        storeImage = in.readString();
        storeUserName = in.readString();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storeName);
        dest.writeString(storeDes);
        dest.writeString(storeLocale);
        dest.writeString(storeTimeOpen);
        dest.writeString(storeTimeClose);
        dest.writeString(storeImage);
        dest.writeString(storeUserName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoreInfo> CREATOR = new Creator<StoreInfo>() {
        @Override
        public StoreInfo createFromParcel(Parcel in) {
            return new StoreInfo(in);
        }

        @Override
        public StoreInfo[] newArray(int size) {
            return new StoreInfo[size];
        }
    };
}
