package com.madevil.gallery;

import java.io.Serializable;

import org.json.JSONObject;

import com.loopj.android.http.PersistentCookieStore;
import com.tencent.tauth.Tencent;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DataShare implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3094732241751722411L;
    public Boolean isLogin = false;

    /*
     * data for tencent open api
     */
    public Tencent tencent = null;
    public String login_msg = "";

    /*
     * data for http request
     */
    public PersistentCookieStore http_cookies = null;

    /*
     * data for user
     */
    public DataUser user = new DataUser(); // current user

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

    private static DataShare mIns = null;

    public static DataShare Ins(Context c) {
	if (mIns == null) {
	    mIns = new DataShare(c);
	}
	return mIns;
    }
}
