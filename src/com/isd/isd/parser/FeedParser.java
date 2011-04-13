package com.isd.isd.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import com.isd.isd.Story;

public class FeedParser {

	public static List<Story> parse(String feedURLstr) {
		URL feedURL;
		try {
			feedURL = new URL(feedURLstr);
		} catch (MalformedURLException e1) {
			throw new RuntimeException("Malformed url: "+feedURLstr);
		}
		final Story curStory = new Story();
        RootElement root = new RootElement("rss");
        final List<Story> stories = new ArrayList<Story>();
        Element channel = root.getChild("channel");
        Element item = channel.getChild("item");
        item.setEndElementListener(new EndElementListener() {
            public void end() {
                stories.add(new Story(curStory));
                blankStory(curStory);
            }
        });
        item.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                curStory.title = body;
            }
        });
        item.getChild("link").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                curStory.url = body;
            }
        });
        item.getChild("enclosure").setStartElementListener(new StartElementListener() {
			public void start(Attributes attributes) {
				curStory.thumbUrl = attributes.getValue("url");
			}
		});
        try {
            Xml.parse(feedURL.openConnection().getInputStream(), 
            		Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stories;
	}
	
	private static void blankStory(Story s) {
		s.text = null;
		s.title = null;
		s.author = null;
		s.date = null;
		s.imgUrl = null;
		s.thumbnail = null;
		s.thumbUrl = null;
		s.url = null;
	}
}
