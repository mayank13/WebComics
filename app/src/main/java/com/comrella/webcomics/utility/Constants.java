/*******************************************************************************
 * Copyright 2011-2013 Mayank Jain, Comrella
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.comrella.webcomics.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.comrella.webcomics.navigation.NavigateToScreen;

/**
 * @author Mayank Jain (mayank13[at]gmail[dot]com)
 */
public final class Constants {

	//public static String[] IMAGES ;
	public static List<String> IMAGES;
	private static NavigateToScreen mTheListener;
	private static String afterTag = null;
	private static NotifyDataUpdateListener dataUpdateListener;
	private static boolean firstCallFlag = true;

	private Constants() {
		}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.comrella.webcomics.FRAGMENT_INDEX";
		public static final String IMAGE_POSITION = "com.comrella.webcomics.IMAGE_POSITION";
	}
	
	public static void getUrlFromRedditService() {
		if(IMAGES == null){
			IMAGES = new ArrayList<String>();
		}
		new HttpGetTask().execute();
	}
	
	private static class HttpGetTask extends AsyncTask<Void, Void, List<String>> {


		private static  String URL_prefix = "http://www.reddit.com/r/comics.json";
		//private static  String URL_prefix = "http://www.reddit.com/r/fffffffuuuuuuuuuuuu.json";
		String URL;
		
		
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

		@Override
		protected List<String> doInBackground(Void... params) {
			
			if(afterTag != null){
				URL = URL_prefix + "?after="+afterTag;
				System.out.println("::New URL:: "+ URL);
			}else{
				URL = URL_prefix;
			}
			HttpGet request = new HttpGet(URL);
			JSONResponseHandler responseHandler = new JSONResponseHandler();
			try {
				return mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			if (null != mClient)
				mClient.close();
			//TODO
				//Constants.IMAGES = result.toArray(new String[result.size()]);
			Constants.IMAGES.addAll(result);
				Log.i("IMAGES", IMAGES.toString());
				if (mTheListener != null && firstCallFlag ) {
					firstCallFlag = false;
		            mTheListener.GotoNextScreen();
		        }else{
				dataUpdateListener.dataUpdated(result);
			}
		}
	}

	public static NavigateToScreen getmTheListener() {
		return mTheListener;
	}

	public static void setmTheListener(NavigateToScreen mTheListener) {
		Constants.mTheListener = mTheListener;
	}

	public static NotifyDataUpdateListener getDataUpdateListener() {
		return dataUpdateListener;
	}

	public static void setDataUpdateListener(
			NotifyDataUpdateListener dataUpdateListener) {
		Constants.dataUpdateListener = dataUpdateListener;
	}

	private static class JSONResponseHandler implements ResponseHandler<List<String>> {

		private static final String CHILDREN_TAG = "children";
		
		private static final String URL = "url";

		@Override
		public List<String> handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			List<String> result = new ArrayList<String>();
			String JSONResponse = new BasicResponseHandler()
					.handleResponse(response);
			try {

				// Get top-level JSON Object - a Map
				JSONObject responseObject = (JSONObject) new JSONTokener(
						JSONResponse).nextValue();

				// Extract value of "posts" key -- a List
				JSONArray posts = responseObject.getJSONObject("data")
						.getJSONArray(CHILDREN_TAG);
				
				//Get the after tag
				
				afterTag = responseObject.getJSONObject("data").getString("after");
				System.out.println("::After tag ::"+afterTag);

				// Iterate over posts list
				for (int idx = 0; idx < posts.length(); idx++) {

					// Get single earthquake data - a Map
					JSONObject post = (JSONObject) posts.get(idx);
					
					// Summarize earthquake data as a string and add it to
					// result
					if ( (post.getJSONObject("data").get(URL)).toString().endsWith(".png") ||  (post.getJSONObject("data").get(URL)).toString().endsWith(".jpg") ){
						result.add((post.getJSONObject("data").get(URL)).toString());
					}
 					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}
