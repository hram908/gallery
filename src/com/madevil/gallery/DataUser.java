package com.madevil.gallery;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class DataUser implements Parcelable {
    public static final String intent = "com.madevil.DataUser.object";

    public String id = "";
    public String nick = "";
    public String intro = "";
    public String avatar = "";
    public int pictureNumber = 0;
    public int moneyNumber = 0;

    @Override
    public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(id);
	dest.writeString(nick);
	dest.writeString(intro);
	dest.writeString(avatar);
	dest.writeInt(pictureNumber);
	dest.writeInt(moneyNumber);
    }

    public DataUser() {

    }

    public DataUser(Parcel in) {
	id = in.readString();
	nick = in.readString();
	intro = in.readString();
	avatar = in.readString();
	pictureNumber = in.readInt();
	moneyNumber = in.readInt();
    }

    public static final Parcelable.Creator<DataUser> CREATOR = new Parcelable.Creator<DataUser>() {
	public DataUser createFromParcel(Parcel in) {
	    return new DataUser(in);
	}

	public DataUser[] newArray(int size) {
	    return new DataUser[size];
	}
    };

}
