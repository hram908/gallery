package com.madevil.gallery;

import org.json.JSONObject;

public class M {

    public static int ecode(JSONObject json_root) {
	return json_root.optInt("ecode", G.ECODE);
    }
    public static String emsg(JSONObject json_root) {
	int ecode = ecode(json_root);
	return "" + ecode + "." + json_root.optString("msg", G.EMSG);
    }

}
