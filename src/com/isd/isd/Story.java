package com.isd.isd;

import android.graphics.drawable.Drawable;

/**
 * Basic container for a news story
 * @author rob
 */
public class Story {

	public String text;
	public String title;
	public String author;
	public String date;
	public String url;
	public String imgUrl;
	public String thumbUrl;
	public String byline;
	
	public Drawable thumbnail;
	public Drawable image;
	
	/**
	 * Copy constructor
	 * @param s The Story to copy
	 */
	public Story(Story s) {
		this.text = s.text;
		this.title = s.title;
		this.author = s.author;
		this.date = s.date;
		this.url = s.url;
		this.imgUrl = s.imgUrl;
		this.thumbUrl = s.thumbUrl;
		this.byline = s.byline;
	}
	
	/**
	 * Default constructor- creates an empty Story
	 */
	public Story() {
		
	}
	
	public boolean imageAvailable() {
		return this.thumbUrl != null;
	}
	
	public boolean thumbnailLoaded() {
		return this.thumbnail != null;
	}
	
	public boolean imageLoaded() {
		return this.image != null;
	}
	
}
