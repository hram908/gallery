package com.madevil.gallery;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.madevil.gallery.R;
import com.madevil.gallery.PictureAdapter.ViewHolder;
import com.origamilabs.library.views.StaggeredGridView;
import com.squareup.picasso.Picasso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

class DataComment {
    public String id = "";
    public String user = "";
    public String content = "";
}

class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<DataComment> mComments;

    public CommentAdapter(Context context) {
	mContext = context;
	mComments = new LinkedList<DataComment>();
    }

    @Override
    public int getCount() {
	return mComments.size();
    }

    @Override
    public Object getItem(int index) {
	return mComments.get(index);
    }

    @Override
    public long getItemId(int index) {
	return index;
    }

    public void addItems(List<DataComment> items) {
	mComments.addAll(items);
    }

    class ViewHolder {
	public int index;
	public ImageView avatarView;
	public TextView contentView;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
	if (view == null) {
	    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	    view = inflater.inflate(R.layout.component_comment, null);
	    ViewHolder holder = new ViewHolder();
	    holder.index = index;
	    holder.avatarView = (ImageView) view
		    .findViewById(R.id.component_comment_avatar);
	    holder.contentView = (TextView) view
		    .findViewById(R.id.component_comment_content);
	    holder.avatarView.setTag((Integer) index);
	    holder.avatarView.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    int index = (Integer) v.getTag();
		    DataComment comment = mComments.get(index);
		    Intent intent = new Intent(mContext, UserActivity.class);
		    intent.putExtra(DataUser.intentTag, comment.user);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    mContext.startActivity(intent);
		}
	    });
	    view.setTag(holder);
	}

	DataComment comment = mComments.get(index);
	ViewHolder holder = (ViewHolder) view.getTag();
	holder.contentView.setText(comment.content);
	String avatar_url = G.Url.getUserAvatar(comment.user);
	Picasso.with(mContext).load(avatar_url).into(holder.avatarView);
	return view;
    }
}

public class CommentActivity extends Activity {
    private CommentAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_comment);
	mAdapter = new CommentAdapter(getApplication());
	mListView = (ListView) this.findViewById(R.id.comment_view_list);
	mListView.setAdapter(mAdapter);
	mAdapter.notifyDataSetChanged();

	LinkedList<DataComment> mComments = new LinkedList<DataComment>();
	DataComment d = new DataComment();
	d.id = "1";
	d.user = "rexliao";
	d.content = "楼主太赞了！！！之前我就喜欢这个图啊！！！";
	mComments.add(d);
	d.id = "2";
	d.user = "rexliao";
	d.content = "顶\n用力顶！\n顶到天！";
	mComments.add(d);

	mAdapter.addItems(mComments);
	mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_comment, menu);
	return true;
    }

}
