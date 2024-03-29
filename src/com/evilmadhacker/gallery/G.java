package com.evilmadhacker.gallery;

import android.net.Uri;

public class G {
    public static final String HOST = "http://app.talebook.org";

    public static class Url {
	public static String index(int page_id) {
	    return HOST + "/index/" + page_id;
	}

	public static String user(String user, int page_id) {
	    return HOST + "/user/" + user + "/info/" + page_id;
	}

	public static String userAvatar(String user) {
	    return HOST + "/avatar/" + user;
	}

	public static String pictureInfo(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/info";
	}

	public static String pictureComment(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/comment/get/0";
	}

	/*
	 * 写操作
	 */
	public static String doPictureDelete(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/delete";
	}
	
	public static String doPictureLike(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/like";
	}
	
	public static String doPictureComment(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/comment/write";
	}

	public static String doPictureDownload(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/download";
	}

	public static String doUpload() {
	    return HOST + "/upload";
	}

	public static String doLogin() {
	    return HOST + "/login";
	}
	
	public static String doHello() {
	    return HOST + "/hello";
	}
	
	public static Uri helpMoney() {
	    return Uri.parse(HOST + "/help/money");
	}
    }

    public static final String TENCENT_APIS = "get_user_info,get_simple_userinfo";
    public static final String TENCENT_APPID = "222222";
    // private static final String APP_ID = "222222";		// qq-sdk test id
    // private static final String APP_ID = "100363349";	// qq-sdk unknown id


    public static final int ECODE = 1314;
    public static final String EMSG = "服务器烧坏了，请稍后重试";

}
