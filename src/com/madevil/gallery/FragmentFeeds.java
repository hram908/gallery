package com.madevil.gallery;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

public class FragmentFeeds extends Fragment {
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_feeds, container, false);
	mContext = this.getActivity().getApplication();
	ViewGroup fatherLayout = (ViewGroup) view.findViewById(R.id.ad);
	ListView listView = (ListView) view.findViewById(R.id.list);
	ExchangeViewManager exchangeViewManager = new ExchangeViewManager(this.getActivity(), new ExchangeDataService());
	exchangeViewManager.addView(fatherLayout, listView);    
	return view;
    }

}
