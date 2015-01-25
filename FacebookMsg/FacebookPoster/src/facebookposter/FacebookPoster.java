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
* Filename: FacebookPoster.java
*
* Description: This file contains main entry.
*
*******************************************************************************/

package facebookposter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class FacebookPoster {
	
	static final String properties_file_name = "settings.properties";
	static final String user_agent = "Mozilla/5.0 (Linux; U; en-US)" +
			" AppleWebKit/528.5+ (KHTML, like Gecko, Safari/528.5+) " +
			"Version/4.0 Kindle/3.0 (screen 600x800; rotate)";

    public static void main(String[] args) throws Exception {
        
        if (args.length == 1)
            System.out.println("The passed message is: " + args[0]);
        else if (args.length > 1) {
        	System.out.println("Please pass only one single String.");
        	System.exit(1);
        }
        else
        {
        	System.out.println("You need to specify the NDEF message.");
        	System.exit(1);
        }
        
        Properties props = loadProperties();
        if (props == null)
        	System.exit(1);
        
        // Creating the http client
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpEntity entity = null;
        
        // Performing the first get to retrieve the initial set of Cookies.
        try {
        	entity = performGet(httpclient, "https://m.facebook.com");
        } catch (ClientProtocolException e) {
        	System.out.println("ClientProtocolException: Cannot reach" +
        			" https://m.facebook.com.");
        	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        } catch (IOException e) {
        	System.out.println("Got an IOException when trying to open " +
        			"https://m.facebook.com");
        	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        }
        
        EntityUtils.consume(entity);
        
//==============================================================================
        
        // Fill the forms with the user credentials.
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("email", props.getProperty("User")));
        nvps.add(new BasicNameValuePair("pass", props.getProperty("Pass")));
        
        // Authenticate with Facebook.
        System.out.println("Authenticating with Facebook.");
        try {
        	entity = performPost(httpclient, "https://m.facebook.com/login.php",
					 nvps);
        } catch (ClientProtocolException e) {
        	System.out.println("ClientProtocolException: Cannot reach" +
        			" https://m.facebook.com/login.php.");
        	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        } catch (IOException e) {
        	System.out.println("Got an IOException when trying to open " +
        			"https://m.facebook.com/login.php");
        	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        }
        
        if (entity != null) {
        	entity = new BufferedHttpEntity(entity);
        	
        	if (entity.getContentLength() < 1)
        		System.out.println("Authentication was successful.");
     	            	   
            OutputStream outputStream = null;
            try {
            	outputStream = new FileOutputStream(new File("fb-out.html"));
                entity.writeTo(outputStream);
            } catch (IOException e) {
            	System.out.println("Warning: Unable to write fb-out.html. Still" +
            			" continuing with program execution.");
            } finally {
            	if (outputStream != null)
            		outputStream.close();
            }
       }
        
        EntityUtils.consume(entity);

//==============================================================================            
       System.out.println("Trying to get the status update composing page"+
    		   " for further processing.");
       
       // Retrieve the composing page for further processing.
       try {
    	   entity = performGet(httpclient, "https://m.facebook.com/composer/");
       } catch (ClientProtocolException e) {
       	System.out.println("ClientProtocolException: Cannot reach" +
    			" https://m.facebook.com/composer/.");
       	httpclient.getConnectionManager().shutdown();
    	System.exit(1);
    } catch (IOException e) {
    	System.out.println("Got an IOException when trying to open " +
    			"https://m.facebook.com/composer/");
    	httpclient.getConnectionManager().shutdown();
    	System.exit(1);
    }
       
       HtmlExtractor extractor = null;
       
       if (entity != null) {
    	   entity = new BufferedHttpEntity(entity);
    	   
    	   extractor = new HtmlExtractor(entity);
    	   
           OutputStream outputStream = null;
           try {
        	   outputStream = new FileOutputStream(new File("fb-out2.html"));
               entity.writeTo(outputStream);
               System.out.println("The recieved compositing page can be viewed" +
                  		" from the file fb-out2.html.");
           } catch (IOException e) {
        	   System.out.println("Warning: Unable to write fb-out2.html. Still" +
        			" continuing with program execution.");
	       } finally {
	    	   if (outputStream != null)
	    		   outputStream.close();
	       }
       }
       
//==============================================================================
       
       //Determine the privacy setting
       String privacy_setting = props.getProperty("Privacy");
       if (privacy_setting.equals("Public"))
    	   privacy_setting = extractor.getPrivacyPublic();
       else if (privacy_setting.equals("Friends"))
    	   privacy_setting = extractor.getPrivacyFriends();
       else if (privacy_setting.equals("Only Me"))
    	   privacy_setting = extractor.getPrivacyOnlyMe();
       else {
    	   System.out.println("Warning: Could not get the right privacy " +
    	   		"settings! Using the standard settings from the profile.");
    	   privacy_setting = null;
       }
       
       // Fill the forms for posting the status update.
       nvps = new ArrayList <NameValuePair>();
       nvps.add(new BasicNameValuePair("status", args[0]));
       nvps.add(new BasicNameValuePair("fb_dtsg", extractor.getFb_dtsg()));
       if (privacy_setting != null)
    	   nvps.add(new BasicNameValuePair("privacy", privacy_setting));
       
       try {
    	   entity = performPost(httpclient, "https://m.facebook.com/a/home.php",
	   				nvps);
       } catch (ClientProtocolException e) {
          	System.out.println("ClientProtocolException: Cannot reach" +
        			" https://m.facebook.com/a/home.php.");
          	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        } catch (IOException e) {
        	System.out.println("Got an IOException when trying to post the " +
        			"status update.");
        	httpclient.getConnectionManager().shutdown();
        	System.exit(1);
        }
       
        if (entity != null) {
        	entity = new BufferedHttpEntity(entity);
       	
	       	if (entity.getContentLength() < 1)
	       		System.out.println("Posting the status update was successful.");
	       	else
	       		System.out.println("Warning: Posting the status update was not" +
	       				" successful. For more details please see fb-out3.html");
    	            	   
            OutputStream outputStream = null;
            try {
            	outputStream = new FileOutputStream(new File("fb-out3.html"));
                entity.writeTo(outputStream);
            } catch (IOException e) {
         	   System.out.println("Warning: Unable to write fb-out3.html. Still" +
           			" continuing with program execution.");
   	       } finally {
   	    	   if (outputStream != null)
   	    		   outputStream.close();
   	       }
      }
       
       EntityUtils.consume(entity);
    }       
//==============================================================================
    
    private static Properties loadProperties() {
    	
    	Properties props = new Properties();
    	
    	try {
            //load a properties file
    		props.load(new FileInputStream(properties_file_name));

    	} catch (IOException ex) {
    		System.out.println("The file could look as follows:");
    		System.out.println("#=============================================");
    		System.out.println("User=fb-mailaddresse");
    		System.out.println("Pass=secret");
    		System.out.println("Privacy=Public <= Possible values: Public, " +
    				"Friends, Only Me");
    		System.out.println("#=============================================");
    		System.out.println("Put the above with real values into a text" +
    				" file called: " + properties_file_name);
    		return null;
    	}
    	
    	// Check if all necessary parameters are set.
    	if (!props.containsKey("User")) {
    		System.out.println("The user name is not set in the config file.");
    		return null;
    	}
    	else if (!props.containsKey("Pass")) {
    		System.out.println("The user name is not set in the config file.");
    		return null;
    	}
    	else if (!props.containsKey("Privacy")) {
    		System.out.println("Warning: The user name is not set in the " +
    				"config file. Using the standard settings from the profile.");
    	}
    	
		return props;    	
    }
    
    private static HttpEntity performGet(DefaultHttpClient httpclient,
    														String url)
    							throws ClientProtocolException, IOException {
    	
    	HttpGet httpget = new HttpGet(url);
        httpget.setHeader("User-Agent", user_agent);
        HttpResponse response = httpclient.execute(httpget);
        
		return response.getEntity();
    }
    
    private static HttpEntity performPost(DefaultHttpClient httpclient,
    									  String url,
    									  List <NameValuePair> nvps)
    							throws ClientProtocolException, IOException {
    	
    	HttpPost httpost = new HttpPost(url);

        httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        httpost.setHeader("User-Agent", user_agent);
        HttpResponse response = httpclient.execute(httpost);
    	
		return response.getEntity();
    }
}
