/*******************************************************************************
* Copyright (c), NXP Semiconductors Gratkorn / Austria
*
* (C)NXP Semiconductors
* All rights are reserved. Reproduction in whole or in part is
* prohibited without the written consent of the copyright owner.
* NXP reserves the right to make changes without notice at any time.
* NXP makes no warranty, expressed, implied or statutory, including but
* not limited to any implied warranty of merchantability or fitness for any
* particular purpose, or that the use will not infringe any third party patent,
* copyright or trademark. NXP must not be liable for any loss or damage
* arising from its use.
********************************************************************************
*
* Filename: HTMLExtractor.java
*
* Description: This file is responsible for parsing the HTML pages.
*
*******************************************************************************/

package facebookposter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlExtractor {
	
	private final HttpEntity entity_;
	private final Document htmlDoc_;
	
	public HtmlExtractor(HttpEntity entity) throws ParseException, IOException {
		entity_ = entity;
		htmlDoc_ = Jsoup.parse(EntityUtils.toString(entity_));
	}
	
	public String getFb_dtsg() {
		
		Elements fb_dtsg = htmlDoc_.select("input[name=fb_dtsg]");
		
		return fb_dtsg.attr("value");
	}
	
	public List<NameValuePair> getPrivacyOptions() {
		
		String privacy_public, privacy_friends, privacy_only_me;
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		// Extracting the privacy settings
		Elements privacy = htmlDoc_.select("a[href*=/privacy/selector/write/]");
		if (privacy.size() > 0) {
     	   Iterator<Element> privacy_it = privacy.iterator();
     	   String result = null;
     	   
     	   while (privacy_it.hasNext()) {
     		   Element temp = privacy_it.next();
     		   if (temp.text().equals("Öffentlich") ||
     			       temp.text().equals("Public")) {
     			   
     			   // Cut the part in front of the interesting part
     			   privacy_public = temp.attr("href").substring((temp.attr("href").lastIndexOf("/privacy/selector/write/?privacy=")+"/privacy/selector/write/?privacy=".length()+3));
     			   // Cut the part after the interesting part
     			   privacy_public = privacy_public.substring(0, privacy_public.indexOf("&redir=")-3);
     			        			   
					try {
						result = URLDecoder.decode(privacy_public, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Finally remove all occurances of \.
					result = result.replace("\\", "");
					
					nvps.add(new BasicNameValuePair("Public", result));
     		   }
     		   else if (temp.text().equals("Freunde") ||
     			            temp.text().equals("Friends") ||
     			            temp.text().equals("Priatelia")) {
     			   
     			   // Cut the part in front of the interesting part
     			   privacy_friends = temp.attr("href").substring((temp.attr("href").lastIndexOf("/privacy/selector/write/?privacy=")+"/privacy/selector/write/?privacy=".length()+3));
     			   // Cut the part after the interesting part
     			   privacy_friends = privacy_friends.substring(0, privacy_friends.indexOf("&redir=")-3);
     			        			   
					try {
						result = URLDecoder.decode(privacy_friends, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Finally remove all occurances of \.
					result = result.replace("\\", "");
					
					nvps.add(new BasicNameValuePair("Friends", result));
     		   }
     		   else if (temp.text().equals("Nur ich") ||
     			            temp.text().equals("Only Me") ||
     			            temp.text().equals("Iba ja")) {
     			   
     			   // Cut the part in front of the interesting part
     			   privacy_only_me = temp.attr("href").substring((temp.attr("href").lastIndexOf("/privacy/selector/write/?privacy=")+"/privacy/selector/write/?privacy=".length()+3));
     			   // Cut the part after the interesting part
     			   privacy_only_me = privacy_only_me.substring(0, privacy_only_me.indexOf("&redir=")-3);
     			        			   
					try {
						result = URLDecoder.decode(privacy_only_me, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Finally remove all occurances of \.
					result = result.replace("\\", "");
					
					nvps.add(new BasicNameValuePair("Only Me", result));   
     		   }
     	   }
        }
        else {
     	   System.out.println("The privacy settings could not be extracted!");
     	   return null;
        }
		
		return nvps;
	}
	
	public String getPrivacyPublic() {
		
		List<NameValuePair> nvps = getPrivacyOptions();
		
		Iterator<NameValuePair> nvps_iter = nvps.iterator();
		while (nvps_iter.hasNext()) {
			NameValuePair bnvp = nvps_iter.next();
			if (bnvp.getName() == "Public")
				return bnvp.getValue();
		}
		
		return null;
	}
	
	public String getPrivacyFriends() {
		
		List<NameValuePair> nvps = getPrivacyOptions();
		
		Iterator<NameValuePair> nvps_iter = nvps.iterator();
		while (nvps_iter.hasNext()) {
			NameValuePair bnvp = nvps_iter.next();
			if (bnvp.getName() == "Friends")
				return bnvp.getValue();
		}
		
		return null;
	}
	
	public String getPrivacyOnlyMe() {
		
		List<NameValuePair> nvps = getPrivacyOptions();
		
		Iterator<NameValuePair> nvps_iter = nvps.iterator();
		while (nvps_iter.hasNext()) {
			NameValuePair bnvp = nvps_iter.next();
			if (bnvp.getName() == "Only Me")
				return bnvp.getValue();
		}
		
		return null;
	}
}
