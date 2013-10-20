package com.madevil.gallery;

import java.io.Serializable;

public class DataUser implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3798278244379962493L;

	public static final String intentTag = "com.myapp.user.object";

	public String id = "";
	public String nick = "";
	public String intro = "";
	public String avatar = "";
	public int pictureNumber = 0;
	public int moneyNumber = 0;
}
