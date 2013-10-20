package com.madevil.gallery;

import org.json.JSONObject;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentLogin  { /*extends FragmentActivity {
    private Tencent mTencent;
    private TextView mMsg;
    private Button mButtonLoginQQ = null;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_index, container, false);
	mButtonLoginQQ = (Button)view.findViewById(R.id.btn_login_qq);
	mMsg = (TextView) view.findViewById(R.id.text_msg);
	mTencent = Tencent.createInstance(G.TENCENT_APPID, getApplication());
	LoginTencent();
	return view;
    }

    public void LoginTencent() {
	mTencent.login((Activity)this, G.TENCENT_APIS, new IUiListener() {
	    @Override
	    public void onCancel() {
		// TODO Auto-generated method stub
		mMsg.setText("cancel");
	    }

	    @Override
	    public void onComplete(JSONObject result) {
		// TODO Auto-generated method stub
		mMsg.setText(result.toString());
	    }

	    @Override
	    public void onError(UiError e) {
		// TODO Auto-generated method stub
		mMsg.setText(e.toString());
	    }

	});
    }*/

}
