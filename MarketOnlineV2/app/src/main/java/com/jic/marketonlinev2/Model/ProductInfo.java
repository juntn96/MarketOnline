package com.jic.marketonlinev2.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jic on 10/9/2016.
 */

public class ProductInfo implements Parcelable {
    private String proName;
    private String proPrice;
    private String proImage;

    public ProductInfo() {
    }

    public ProductInfo(String proName, String proPrice, String proImage) {
        this.proName = proName;
        this.proPrice = proPrice;
        this.proImage = proImage;
    }

    protected ProductInfo(Parcel in) {
        proName = in.readString();
        proPrice = in.readString();
        proImage = in.readString();
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProPrice() {
        return proPrice;
    }

    public void setProPrice(String proPrice) {
        this.proPrice = proPrice;
    }

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }

    public static Creator<ProductInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(proName);
        dest.writeString(proPrice);
        dest.writeString(proImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductInfo> CREATOR = new Creator<ProductInfo>() {
        @Override
        public ProductInfo createFromParcel(Parcel in) {
            return new ProductInfo(in);
        }

        @Override
        public ProductInfo[] newArray(int size) {
            return new ProductInfo[size];
        }
    };
}
