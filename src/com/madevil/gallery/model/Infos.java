package com.madevil.gallery.model;

import java.util.List;

public class Infos {
	private String			newsLast	= "0";
	private int				type		= 0;
	private List<DataPicture>	newsInfos;

	public String getNewsLast() {
		return newsLast;
	}

	public void setNewsLast(String newsLast) {
		this.newsLast = newsLast;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<DataPicture> getNewsInfos() {
		return newsInfos;
	}

	public void setNewsInfos(List<DataPicture> newsInfos) {
		this.newsInfos = newsInfos;
	}

}
