package com.evilmadhacker.gallery;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evilmadhacker.gallery.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

class DataComment {
    public String id = "";
    public String user_nick = "";
    public String time = "";
    public String content = "";
    public DataUser user = new DataUser();
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

    public void addLast(DataComment item) {
	mComments.addLast(item);
    }

    public void addFirst(DataComment item) {
	mComments.addFirst(item);
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
		    Intent intent = new Intent(mContext, ActivityUser.class);
		    intent.putExtra(DataUser.intent, comment.user);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    mContext.startActivity(intent);
		}
	    });
	    view.setTag(holder);
	}

	DataComment comment = mComments.get(index);
	ViewHolder holder = (ViewHolder) view.getTag();
	holder.contentView.setText(comment.content);
	String avatar_url = G.Url.userAvatar(comment.user.id);
	Log.d("http", "avatar url=" + avatar_url);
	Picasso.with(mContext).load(avatar_url).into(holder.avatarView);
	return view;
    }
}

public class ActivityComment extends BasicActivity {
    private CommentAdapter mAdapter = null;
    private ListView mListView = null;
    private DataPicture mPicture = null;
    private Context mContext = null;
    private EditText mText = null;
    private DataComment mMyComment = null;
    private DataShare share = null;
    private Button mButtonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_comment);
	this.setTitle("0条评论");
	this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	mContext = this.getApplicationContext();
	share = DataShare.Ins(mContext);

	mText = (EditText) this.findViewById(R.id.comment_text);
	mButtonSend = (Button) this.findViewById(R.id.comment_btn_send);

	mAdapter = new CommentAdapter(getApplication());
	mListView = (ListView) this.findViewById(R.id.comment_view_list);
	mListView.setAdapter(mAdapter);
	mAdapter.notifyDataSetChanged();

	Intent intent = getIntent();
	mPicture = intent.getParcelableExtra(DataPicture.intent);

	Http.With(mContext).get(G.Url.pictureComment(mPicture.id),
		new JsonHttpResponseHandler() {
		    @Override
		    public void onSuccess(JSONObject json_data) {
			doSuccessGetComments(json_data);
		    }
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_comment, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    this.finish();
	    // NavUtils.navigateUpFromSameTask(this);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    public void onClick_comment_btn_add(View view) {
	mButtonSend.setEnabled(false);
	String text = mText.getText().toString();
	text = text.replace("\n", " ").replace("\r", "").trim();
	if (text.length() == 0) {
	    Toast.makeText(mContext, "请先填写评论", Toast.LENGTH_SHORT).show();
	    mButtonSend.setEnabled(true);
	    return;
	}

	// save to mem
	mMyComment = new DataComment();
	mMyComment.id = "";
	mMyComment.user = share.user;
	mMyComment.content = text;

	RequestParams params = new RequestParams();
	params.put("content", text);
	String url = G.Url.doPictureComment(mPicture.id);
	Http.With(mContext).post(url, params, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		mButtonSend.setEnabled(true);
		mAdapter.addLast(mMyComment);
		mAdapter.notifyDataSetChanged();
		mText.setText("");
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		mButtonSend.setEnabled(true);
	    }
	});
    }

    public void doSuccessGetComments(JSONObject json_data) {
	LinkedList<DataComment> comments = new LinkedList<DataComment>();
	try {
	    JSONArray json_comments = json_data.getJSONArray("comments");
	    for (int i = 0; i < json_comments.length(); i++) {
		JSONObject obj = json_comments.getJSONObject(i);
		DataComment c = new DataComment();
		c.id = "";
		c.user.id = obj.getString("userid");
		c.content = obj.getString("content");
		c.user_nick = obj.getString("nick");
		c.time = obj.getString("time");
		comments.add(c);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.e("ActivityComment", "get comments fail");
	}
	this.setTitle(comments.size()+"条评论");
	mAdapter.addItems(comments);
	mAdapter.notifyDataSetChanged();

    }

}
