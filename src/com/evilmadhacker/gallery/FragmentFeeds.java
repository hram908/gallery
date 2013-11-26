package com.evilmadhacker.gallery;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.evilmadhacker.gallery.R;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

public class FragmentFeeds extends TrackedFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_feeds, container, false);
	ViewGroup fatherLayout = (ViewGroup) view.findViewById(R.id.ad);
	ListView listView = (ListView) view.findViewById(R.id.list);
	ExchangeViewManager exchangeViewManager = new ExchangeViewManager(this.getActivity(), new ExchangeDataService());
	exchangeViewManager.addView(fatherLayout, listView);    
	return view;
    }

}
