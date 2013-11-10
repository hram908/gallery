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

    private String id = "";
    private String title = "";
    private String content = "";
    private String url = "";
    private String url_s = "";
    private String url_m = "";
    private int height = 0;
    private int width = 0;
    private int commentNumber = 0;
    private int likeNumber = 0;
    private int downloadNumber = 0;

    public DataUser user = new DataUser();
    
    public DataPicture() {
	
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public int getWidth() {
	return width;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public String getTitle() {
	if ( title.length() > 0 )
	    return title;
	return "很喜欢的一张写真";
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getCommentNumber() {
	return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
	this.commentNumber = commentNumber;
    }

    public int getLikeNumber() {
	return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
	this.likeNumber = likeNumber;
    }

    public int getDownloadNumber() {
	return downloadNumber;
    }

    public void setDownloadNumber(int downloadNumber) {
	this.downloadNumber = downloadNumber;
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

    public String getUrl() {
	return url;
    }

    public String getSmallUrl() {
	return url_s;
    }

    public String getMiddleUrl() {
	return url_m;
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
