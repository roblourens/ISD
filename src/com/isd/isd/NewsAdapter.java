package com.isd.isd;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter that displays a list of Stories
 * @author rob
 */
public class NewsAdapter extends BaseAdapter {
	private final static int NUM_ROW_TYPES = 2;
	private final static int FEATURE_TYPE = 0;
	private final static int NORMAL_TYPE = 1;
	
	private List<Story> stories;
	private LayoutInflater inflater;
	
	public NewsAdapter(Context c, List<Story> stories) {
		setStories(stories);
		this.inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return stories.size();
	}

	@Override
	public Object getItem(int position) {
		return stories.get(position);
	}
	
	@Override
    public int getViewTypeCount() {
        return NUM_ROW_TYPES;
    }
	
    @Override
    public int getItemViewType(int position) {
    	if (position == 0)
    		return FEATURE_TYPE;
    	else
    		return NORMAL_TYPE;
    }

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setStories(List<Story> stories) {
		if (stories != null) {
			this.stories = stories;
			// Begin image downloads
			for (Story s : stories)
				if (s.imageAvailable()) {
					ThumbDownloadTask task = new ThumbDownloadTask(this);
					task.execute(s);
				}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (position == 0)
				convertView = inflater.inflate(R.layout.feature_news_row, null);
			else
				convertView = inflater.inflate(R.layout.news_row, null);
		}
		
		Story s = stories.get(position);

		// Must always set textView size with setLines
		// and set gravity to center text
		// http://stackoverflow.com/questions/5225428/layout-messed-up-in-listview-after-scrolling/5226794
		TextView titleView = (TextView)convertView.findViewById(R.id.title);
		titleView.setLines(2);
		titleView.setGravity(Gravity.CENTER_VERTICAL);
		titleView.setText(s.title);
		
		if (position == 0) {
			ImageView featureImgView = (ImageView)convertView.findViewById(R.id.feature_img);
			featureImgView.setImageDrawable(s.image);
		}
		else {
			// Set up image, if exists
			ImageView thumbView = (ImageView)convertView.findViewById(R.id.thumbnail);
			if (s.imageAvailable()) {
				thumbView.setImageDrawable(s.thumbnail);
				thumbView.setVisibility(View.VISIBLE);
				Log.d("isd", "set image for " + s.title);
			}
			else {
				thumbView.setVisibility(View.GONE);
				//Log.d("isd", "no image for " + s.title);
			}
		}
		return convertView;
	}
	
	private class ThumbDownloadTask extends AsyncTask<Story, Void, Void> {
		
		private BaseAdapter adapter;
		
		public ThumbDownloadTask(BaseAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		protected Void doInBackground(Story... params) {
			Story s = params[0];
			ImageDownloader downloader = new ImageDownloader(s.thumbUrl);
			s.thumbnail = downloader.go();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			adapter.notifyDataSetChanged();
		}
	}
}
