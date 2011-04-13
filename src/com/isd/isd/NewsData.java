package com.isd.isd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isd.isd.parser.FeedParser;
import com.isd.isd.parser.StoryParser;

public class NewsData {
	private List<Story> stories;
	private final int NUM_STORIES = 30;
	private final String feedURLfstring = 
		"http://iowastatedaily.com/search/?q=&t=article&l=%d"+
		"&d=&d1=&d2=&s=start_time&sd=desc&c[]=%s&f=rss";
	
	private NewsData(String category) {
		String feedURL = String.format(feedURLfstring, NUM_STORIES, category);
		stories = FeedParser.parse(feedURL);
		
		// Move a Story with an image to be the first
		for (int i=0; i<stories.size(); i++) {
			Story s = stories.get(i);
			if (s.imageAvailable()) {
				stories.remove(i);
				stories.add(0, s);
				break;
			}
		}
	}
	
	public List<Story> getStories() {
		return stories;
	}
	
	public Story loadedStoryAtIndex(int i) {
		Story s = stories.get(i);
		StoryParser.fillData(s);
		ImageDownloader downloader = new ImageDownloader(s.imgUrl);
		s.image = downloader.go();
		
		return stories.get(i);
	}
	
	public Story storyAtIndex(int i) {
		return stories.get(i);
	}
	
	private static Map<String, NewsData> dataForCategory =
		new HashMap<String, NewsData>();
	public static NewsData getNewsData(String category, boolean refresh) {
		if (!dataForCategory.containsKey(category) || refresh)
			dataForCategory.put(category, new NewsData(category));
		
		return dataForCategory.get(category);
	}
}
