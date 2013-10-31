package com.madevil.gallery;

import java.io.Serializable;

import android.util.Log;

public class DataPicture implements Serializable {
    public static final String intentTag = "com.madevil.picture.object";

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
	return title;
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


    private static final String[] tags = {"_s.", "_m.", "."};
    public void setUrl(String s) {
	int pos = -1;
	String tag = "";
	for ( int i = 0; i < tags.length; ++ i ) {
	    tag = tags[i];
	    pos = s.lastIndexOf(tag);
	    if ( pos > 0 ) {
		break;
	    }
	}
	url = s.substring(0, pos) + "." + s.substring(pos + tag.length());
	url_s = s.substring(0, pos) + "_s." + s.substring(pos + tag.length());
	url_m = s.substring(0, pos) + "_m." + s.substring(pos + tag.length());
	Log.d("DataPicture", "url="+url);
	Log.d("DataPicture", "url_s="+url_s);
	Log.d("DataPicture", "url_m="+url_m);
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

}
