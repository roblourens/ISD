package com.isd.isd.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.sax.TextElementListener;
import android.util.Log;
import android.util.Xml;

import com.isd.isd.Story;

public class StoryParser {
	
	private enum ParseState {
		BODY, PHOTO, BYLINE, OTHER
	}
	
	private final static String PHOTO_CLASS = "photo";
	private final static String BODY_CLASS = "mobile-story-text";
	private final static String BYLINE_CLASS = "byline";
	
	private static final String mobileStoryUrlBase =
		"http://m.iowastatedaily.com/mobile";
	
	public static void fillData(final Story s) {
		try {
			doParse(s);
		} catch (XmlPullParserException e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void doParse(final Story s)
					throws XmlPullParserException, MalformedURLException, IOException {
		// Set up URL
		URL storyURL = new URL(mobileStoryUrlBase +
				s.url.substring(s.url.lastIndexOf('/')));
		
		// Set up parser
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		XmlPullParser xpp = factory.newPullParser();
		
		Reader input = new InputStreamReader(storyURL.openConnection()
													 .getInputStream());
		xpp.setInput(input);
		int eventType = xpp.getEventType();
		ParseState state = ParseState.OTHER;
		
		// parse
		StringBuilder textSB = new StringBuilder();
		// so when a tag ends we know exactly which tag is ending
		int depth = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {
	         if(eventType == XmlPullParser.START_TAG) {
	        	 
	        	 // Body starts
	        	 if (getAttr(xpp, "class").equals(BODY_CLASS)) {
	        		 state = ParseState.BODY;
	        		 depth = 0; // the start of the body is the reference depth
	        	 }
	        	 
	        	 // Photo div starts
	        	 else if (getAttr(xpp, "class").equals(PHOTO_CLASS))
	        		 state = ParseState.PHOTO;
	        	 
	        	 // Photo img found
	        	 else if (state == ParseState.PHOTO && xpp.getName().equals("img")) {
	        		 s.imgUrl = xpp.getAttributeValue(null, "src");
	        		 state = ParseState.OTHER;
	        	 }
	        	 
	        	 // byline <p> found
	        	 else if (getAttr(xpp, "class").equals(BYLINE_CLASS)) {
	        		 state = ParseState.BYLINE;
	        	 }
	        	 
	        	 depth++;
	         } else if (eventType == XmlPullParser.TEXT) {
	        	 // Found body text
	             if (state == ParseState.BODY) {
	            	 if (xpp.getText() != null) {
	            		 Log.d("isd", "body text: " + xpp.getText());
		            	 textSB.append(xpp.getText().replace("\n", " ")
		            			 					.replace("\t", ""));
	            	 }
	             }
	             else if (state == ParseState.BYLINE) {
	            	 Log.d("isd", "byline: " + xpp.getText());
	            	 s.byline = xpp.getText().trim().replace(" |  ", "\n");
	             }
	             
	         } else if(eventType == XmlPullParser.END_TAG) {
	        	 depth--;
	        	 Log.d("isd", "ended " + xpp.getName() + " with depth " + depth);
	        	  
	        	 // Body ends
	             if (state == ParseState.BODY && depth==0) {
	            	 // Sometimes paragraphs will start with a space b/c
	            	 // we replace \n with " ". This fixes that.
	            	 s.text = textSB.toString().trim().replace("\n ", "\n");
	            	 state = ParseState.OTHER;
	             }
	             
	             else if (state == ParseState.BODY) {
	            	 if (xpp.getName().equals("p"))
	            		 textSB.append("\n\n");
	             }
	             
	             else if (state == ParseState.BYLINE)
	            	 state = ParseState.OTHER;
	         }
	         try {
	        	 eventType = xpp.next();
	         } catch (XmlPullParserException e) {
	        	 // LE PROBLEM: barfs on &copy; no idea why. Reads &amp; fine.
	        	 // Tried setting encoding to UTF-8 manually in the InputStreamReader
	        	 // but it picks up UTF-8 anyway on its own. Tried using the
	        	 // xpp.setInput(InputStream, encoding) method but no difference.
	        	 // For now just ignore the exception
	         }
	    }
	}
	
	private static String getAttr(XmlPullParser xpp, String name) {
		String value = xpp.getAttributeValue(null, name);
		return (value == null ? "" : value);
	}
	
	
	
	
	
	
	
	public static void fillData2(final Story s) {
		URL storyURL;
		try {
			storyURL = new URL(mobileStoryUrlBase +
					s.url.substring(s.url.lastIndexOf('/')));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed story url");
		}
		
		RootElement html = new RootElement("http://www.w3.org/1999/xhtml", "html");
		Element body = html.getChild("body");
		
		body.setTextElementListener(new TextElementListener() {
			
			private ParseState state = ParseState.OTHER;
			/** We don't know which element we are at in end(). So to know
			 * when the body div is ending, keep track of how many elements
			 * have started and ended
			 */
			private int depth = 0;
			
			@Override
			public void start(Attributes attributes) {
				if (attributes.getValue("class").equals(PHOTO_CLASS))
					state = ParseState.PHOTO;
				else if (state == ParseState.PHOTO)
					s.imgUrl = attributes.getValue("src");
				else if (attributes.getValue("class").equals(BODY_CLASS)) {
					state = ParseState.BODY;
					s.text = "";
				}
				else if (state == ParseState.BODY)
					depth++;
			}
			
			@Override
			public void end(String body) {
				if (state == ParseState.BODY) {
					if (depth==0)
						state = ParseState.OTHER;
					else {
						s.text = s.text + body;
						depth--;
					}
				}
				
			}
		});
		
		body.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				Log.d("isd", "class2: " + attributes.getValue("class"));
			}
			
		});
		
		try {
			Xml.parse(storyURL.openConnection().getInputStream(),
					Xml.Encoding.UTF_8, html.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
