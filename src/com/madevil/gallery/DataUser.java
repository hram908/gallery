package com.madevil.gallery;

import java.io.Serializable;

public class DataUser implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3798278244379962493L;

	public static final String intentTag = "com.myapp.user.object";

	private String id = "";
	private String nick = "";
	private String intro = "";
	private int pictureNumber = 0;
	private int moneyNumber = 0;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public int getPictureNumber() {
		return pictureNumber;
	}
	public void setPictureNumber(int pictureNumber) {
		this.pictureNumber = pictureNumber;
	}
	public int getMoneyNumber() {
		return moneyNumber;
	}
	public void setMoneyNumber(int moneyNumber) {
		this.moneyNumber = moneyNumber;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}

}
