package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.madevil.gallery.R;
import com.madevil.gallery.model.DataPicture;
import com.madevil.gallery.model.Globals;
import com.madevil.util.Helper;
import com.madevil.util.ImageFetcher;
import com.origamilabs.library.views.StaggeredGridView;

/**
 *
 * This will not work so great since the heights of the imageViews are
 * calculated on the iamgeLoader callback ruining the offsets. To fix this try
 * to get the (intrinsic) image width and height and set the views height
 * manually. I will look into a fix once I find extra time.
 *
 * @author Maurycy Wojtowicz
 *
 */
public class MainActivity extends Activity {
    public static final String HOST = "http://nitui.tk:8000";

    private ImageFetcher mImageFetcher;
    private PictureAdapter mAdapter;
    private StaggeredGridView mGridView;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageFetcher = new ImageFetcher(this, 240);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);

        mGridView = (StaggeredGridView) this.findViewById(R.id.main_view_staggered_grid);
        mGridView.setItemMargin(1,1,1,1); // set the GridView margin

        mAdapter = new PictureAdapter(MainActivity.this, mImageFetcher);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        AddItemToContainer(1);
        AddItemToContainer(2);
        AddItemToContainer(3);
    }

    private void AddItemToContainer(int pageindex) {
	String url = Globals.Url.getIndexByNew(pageindex);
        Log.d("MainActivity", "current url:" + url);
        ContentTask task = new ContentTask(this);
        task.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private class ContentTask extends AsyncTask<String, Integer, List<DataPicture>> {
        private Context mContext;
        private int ecode = 0;
        private String msg = "";

        public ContentTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected List<DataPicture> doInBackground(String... params) {
            try {
                return parseNewsJSON(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DataPicture> result) {
		if ( ecode != 0 ) {
                Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
		}
            mAdapter.addItems(result);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
        }

        public List<DataPicture> parseNewsJSON(String url) throws IOException {
            List<DataPicture> duitangs = new ArrayList<DataPicture>();
            String json = "";
            if (Helper.checkConnection(mContext)) {
                try {
                    json = Helper.getStringFromUrl(url);
                } catch (Exception e) {
                    Log.e("IOException is : ", e.toString());
                    e.printStackTrace();
                    msg = "服务器出错";
                    ecode = 1;
                    return duitangs;
                }
            }
            Log.d("MainActiivty", "json:" + json);

            try {
                if (null != json) {
                    JSONObject json_root = new JSONObject(json);
                    JSONObject json_data = json_root.getJSONObject("data");
                    JSONArray json_pics = json_data.getJSONArray("pics");
                    for (int i = 0; i < json_pics.length(); i++) {
                        JSONObject obj = json_pics.getJSONObject(i);
                        DataPicture picture = new DataPicture();
                        picture.setId(obj.optString("pid", ""));
                        picture.setUrl(obj.optString("url", ""));
                        picture.setTitle(obj.optString("title", ""));
                        picture.setContent(obj.optString("content", ""));
                        picture.setCommentNumber(obj.optInt("comment_num", 0));
                        picture.setLikeNumber(obj.optInt("like", 0));
                        picture.setDownloadNumber(obj.optInt("download", 0));
                        picture.setHeight(obj.optInt("height",0));
                        picture.setWidth(obj.optInt("width"));
                        duitangs.add(picture);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ecode = 2;
                msg = "服务器数据有误";
            }
            return duitangs;
        }
    }

}
