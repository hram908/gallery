package com.madevil.gallery;

import java.io.Serializable;

public class DataPicture implements Serializable {
	public static final String intentTag = "com.myapp.picture.object";

	/**
	 *
	 */
	private static final long serialVersionUID = 3059978754733451579L;

	private String id = "";
	private String title = "";
	private String content = "";
	private String url = "";
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
