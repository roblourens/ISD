package com.isd.isd;

import java.io.IOException;
import java.net.MalformedURLException;

import android.graphics.drawable.Drawable;

public class ImageDownloader {
	
	private String imgUrl;
	
	public ImageDownloader(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public Drawable go() {
		try {
			if (imgUrl != null) {
				Drawable img = Drawable.createFromStream(((java.io.InputStream)
						new java.net.URL(imgUrl).getContent()), "name");
				return img;
			}
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
