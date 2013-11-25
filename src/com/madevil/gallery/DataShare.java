package com.madevil.gallery;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loopj.android.http.PersistentCookieStore;
import com.tencent.tauth.Tencent;

public class DataShare implements Serializable {
    /**
     *
     */
    public static final String intendTag = "com.madevil.data.share";
    private static final long serialVersionUID = 3094732241751722411L;

    public Boolean is_login = false;
    public String login_info = ""; // tencent login info
    public Tencent tencent = null; // tencent open api
    public PersistentCookieStore http_cookies = null; // http cookies
    public DataUser user = new DataUser(); // current user
    public String pictures_json = "";
    public int picture_index;

    public LinkedList<DataPicture> pictures = new LinkedList<DataPicture>();

    // public LinkedList<DataPicture> pictures_user = new
    // LinkedList<DataPicture>();

    private DataShare(Context c) {
	tencent = Tencent.createInstance(G.TENCENT_APPID, c);
	http_cookies = new PersistentCookieStore(c);

	try {
	    TelephonyManager telephonyManager = (TelephonyManager) c
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    String imei = telephonyManager.getDeviceId();
	    user.id = "imei_" + imei;
	} catch (Exception e) {
	    user.id = "guest";
	}
    }

    public void dump(SharedPreferences settings) {
	SharedPreferences.Editor editor = settings.edit();
	editor.putBoolean("is_login", is_login);
	editor.putString("login_info", login_info);
	editor.putString("user.id", user.id);
	editor.putString("user.nick", user.nick);
	editor.putString("user.avatar", user.avatar);
	editor.putString("pictures_json", pictures_json);
	editor.commit();
    }

    public void load(SharedPreferences settings) {
	is_login = settings.getBoolean("is_login", is_login);
	login_info = settings.getString("login_info", login_info);
	user.id = settings.getString("user.id", user.id);
	user.nick = settings.getString("user.nick", user.nick);
	user.avatar = settings.getString("user.avatar", user.avatar);
	pictures_json = settings.getString("pictures_json", pictures_json);

	Log.d("DataShare", "load is_login=" + is_login);
    }

    private static DataShare mIns = null;

    public static DataShare Ins(Context c) {
	if (mIns == null) {
	    mIns = new DataShare(c);

	}
	return mIns;
    }
}
