package com.isd.isd;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ISD extends ActivityGroup {
	private TabHost mTabHost;

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(getLocalActivityManager());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// construct the tabhost
		setContentView(R.layout.main);

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab("News");
		setupTab("Opinion");
		setupTab("Sports");
		setupTab("Business");
	}

	/**
	 * Creates a tab and an Intent for that tab, adds to the TabHost
	 * @param tag The name of the new tab, and source for content
	 */
	private void setupTab(final String tag) {
		// Intent containing news story list activity
		// Has news category as Extra
		Intent content = new Intent(this, NewsActivity.class);
		content.putExtra("category", tag);
		
		// Create a view for the tab and set that as the indicator of the tab
		// This bypasses the default tab look and lets us customize the tab
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag)
									 .setIndicator(tabview)
									 .setContent(content);
		mTabHost.addTab(setContent);
	}

	/**
	 * Create a for the tab from tabs_bg, set its text 
	 * @param context The context of the program
	 * @param text The label of the tab
	 * @return The tab view
	 */
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	public void refresh(View view) {
		String tag = mTabHost.getCurrentTabTag();
		NewsActivity curNewsActivity = (NewsActivity)getLocalActivityManager()
														.getActivity(tag);
		curNewsActivity.loadData();
	}
}
