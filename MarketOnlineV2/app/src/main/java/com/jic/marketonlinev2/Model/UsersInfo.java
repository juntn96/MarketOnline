package com.jic.marketonlinev2.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jic on 10/3/2016.
 */

public class UsersInfo implements Parcelable {
    private String name;
    private String phone;
    private String mail;
    private String avatar;

    public UsersInfo() {
    }

    public UsersInfo(String name, String phone, String mail, String avatar) {
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.avatar = avatar;
    }

    public UsersInfo(Parcel in) {
        this.name = in.readString();
        this.phone = in.readString();
        this.mail = in.readString();
        this.avatar = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(mail);
        dest.writeString(avatar);
    }

    public static final Creator<UsersInfo> CREATOR = new Creator<UsersInfo>() {
        @Override
        public UsersInfo createFromParcel(Parcel in) {
            return new UsersInfo(in);
        }

        @Override
        public UsersInfo[] newArray(int size) {
            return new UsersInfo[size];
        }
    };
}
