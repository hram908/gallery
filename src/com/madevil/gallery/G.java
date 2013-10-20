package com.madevil.gallery;

import com.loopj.android.http.AsyncHttpClient;

public class G {
    public static final String HOST = "http://174.140.168.22:8000";
    public static AsyncHttpClient http = new AsyncHttpClient();

    public static class Url {
	public static String getIndexByNew(int page_id) {
	    return HOST + "/index/new/" + page_id;
	}

	public static String getIndexByHot(int page_id) {
	    return HOST + "/index/hot/" + page_id;
	}

	public static String getUser(String user, int page_id) {
	    return HOST + "/me/" + page_id;
	}

	public static String getPictureDetail(String picture_id) {
	    return HOST + "/pic/" + picture_id + "/info";
	}

	public static String getUserAvatar(String user) {
	    return HOST + "/avatar/" + user;
	}

	public static String getLogin() {
	    return HOST + "/login";
	}
    }

    public static final String TENCENT_APIS = "get_user_info,get_simple_userinfo";
    public static final String TENCENT_APPID = "222222";
    // private static final String APP_ID = "222222";		// qq-sdk test id
    // private static final String APP_ID = "100363349";	// qq-sdk unknown id


    public static final int ECODE = 1314;
    public static final String EMSG = "服务器烧坏了，请稍后重试";

}
