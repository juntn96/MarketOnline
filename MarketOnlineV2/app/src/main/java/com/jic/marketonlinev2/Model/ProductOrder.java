package com.jic.marketonlinev2.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jic on 10/11/2016.
 */

public class ProductOrder implements Parcelable {
    private String proName;
    private String proPrice;
    private int quantity;

    public ProductOrder() {
    }

    public ProductOrder(String proName, String proPrice, int quantity) {
        this.proName = proName;
        this.proPrice = proPrice;
        this.quantity = quantity;
    }

    protected ProductOrder(Parcel in) {
        proName = in.readString();
        proPrice = in.readString();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(proName);
        dest.writeString(proPrice);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductOrder> CREATOR = new Creator<ProductOrder>() {
        @Override
        public ProductOrder createFromParcel(Parcel in) {
            return new ProductOrder(in);
        }

        @Override
        public ProductOrder[] newArray(int size) {
            return new ProductOrder[size];
        }
    };

    public static Creator<ProductOrder> getCREATOR() {
        return CREATOR;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return proName + " " + proPrice + " " + quantity;
    }
}
