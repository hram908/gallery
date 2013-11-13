package com.madevil.gallery;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DataPicture implements Parcelable {
    public static final String intent = "com.madevil.DataPicture.object";
    public static final String intentPictures = "com.madevil.DataPicture.pictures";
    public static final String intentIndex = "com.madevil.DataPicture.pictures.index";

    /**
	 *
	 */
    private static final long serialVersionUID = 3059978754733451579L;

    public String id = "";
    public String title = "";
    public String content = "";
    public String url = "";
    public String url_s = "";
    public String url_m = "";
    public int height = 0;
    public int width = 0;
    public int commentNumber = 0;
    public int likeNumber = 0;
    public int downloadNumber = 0;
    public DataUser user = new DataUser();
    public Boolean hasLike = false;
    public Boolean hasComment = false;
    public Boolean hasDownload = false;

    public DataPicture() {	
    }

    public String getTitle() {
	if ( title.length() > 0 )
	    return title;
	return "很喜欢的一张写真";
    }

    private static final String[] tags = { "_s.", "_m.", "." };

    public void setUrl(String s) {
	int pos = -1;
	String tag = "";
	for (int i = 0; i < tags.length; ++i) {
	    tag = tags[i];
	    pos = s.lastIndexOf(tag);
	    if (pos > 0) {
		break;
	    }
	}
	url = s.substring(0, pos) + "." + s.substring(pos + tag.length());
	url_s = s.substring(0, pos) + "_s." + s.substring(pos + tag.length());
	url_m = s.substring(0, pos) + "_m." + s.substring(pos + tag.length());
    }

    @Override
    public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(url);
	dest.writeString(url_m);
	dest.writeString(url_s);
	dest.writeString(id);
	dest.writeString(title);
	dest.writeString(content);
	dest.writeInt(height);
	dest.writeInt(width);
	dest.writeInt(likeNumber);
	dest.writeInt(commentNumber);
	dest.writeInt(downloadNumber);
	dest.writeInt(hasLike?1:0);
	dest.writeInt(hasComment?1:0);
	dest.writeInt(hasDownload?1:0);
	dest.writeParcelable(user, 0);
    }

    private DataPicture(Parcel in) {
	url = in.readString();
	url_m = in.readString();
	url_s = in.readString();
	id = in.readString();
	title = in.readString();
	content = in.readString();
	height = in.readInt();
	width = in.readInt();
	likeNumber = in.readInt();
	commentNumber = in.readInt();
	downloadNumber = in.readInt();
	hasLike = in.readInt() > 0;
	hasComment = in.readInt() > 0;
	hasDownload = in.readInt() > 0;
	user = in.readParcelable(DataUser.class.getClassLoader());
    }

    public static final Parcelable.Creator<DataPicture> CREATOR = new Parcelable.Creator<DataPicture>() {
	public DataPicture createFromParcel(Parcel in) {
	    return new DataPicture(in);
	}

	public DataPicture[] newArray(int size) {
	    return new DataPicture[size];
	}
    };

}
