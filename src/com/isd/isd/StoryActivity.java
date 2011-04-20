package com.isd.isd;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class StoryActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get category and index of story to be displayed
		String cat = getIntent().getCharSequenceExtra("category").toString();
		int storyIndex = getIntent().getIntExtra("index", 0);
		
		// Get Story object
		Story s = NewsData.getNewsData(cat, false).loadedStoryAtIndex(storyIndex);
		
		// Setup view
		if (s.imageAvailable()) {
			setContentView(R.layout.image_story);
			ImageView image = (ImageView)findViewById(R.id.image);
			image.setImageDrawable(s.image);
		}
		else
			setContentView(R.layout.story);
		
		
		TextView headline = (TextView)findViewById(R.id.headline);
		headline.setText(s.title);
		
		TextView byline = (TextView)findViewById(R.id.byline);
		byline.setText(s.byline);
		
		TextView storyText = (TextView)findViewById(R.id.story_text);
		storyText.setText(s.text);
		
		ScrollView scroll = (ScrollView)findViewById(R.id.story_scroll);
		scroll.setScrollbarFadingEnabled(true);
	}
}
