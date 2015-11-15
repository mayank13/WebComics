/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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
package com.comrella.webcomics.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comrella.webcomics.R;
import com.comrella.webcomics.database.DatabaseHandler;
import com.comrella.webcomics.database.Favorite;
import com.comrella.webcomics.utility.FragmentLifecycle;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class FavoritesImageListFragment extends AbsListViewBaseFragment implements FragmentLifecycle{

	public static final int INDEX = 0;
	private final static String fileName = "FavoriteURLs.txt";

	List<String> imageUrls;
	ImageAdapter mAdapter;
	DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> favUrls = new ArrayList<String>();
		favUrls = getFavoriteImageUrlsFromDatabase(getActivity().getApplicationContext());
		imageUrls = new ArrayList<String>();
		imageUrls.addAll(favUrls);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_list, container,
				false);
		listView = (ListView) rootView.findViewById(android.R.id.list);
		mAdapter = new ImageAdapter();
		((ListView) listView).setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});
		return rootView;
	}
	

	// Adding method to read images from the Internal Storage

	public String[] getFavoriteImageUrls() {
		String favUrls[];
		if (!getActivity().getApplicationContext().getFileStreamPath(fileName)
				.exists()) {

			try {

				writeFile();

			} catch (FileNotFoundException e) {
				Log.i("WEBCOMICS", "FileNotFoundException");
			}
		}

		// Read the data from the text file and display it
		try {

			favUrls = readFile();
			return favUrls;

		} catch (IOException e) {
			Log.i("WEBCOMICS", "IOException");
		}
		return null;
	}

	// Method to read favorite image urls from SQL Lite Database

	// Adding method to read images from the Internal Storage

	public List<String> getFavoriteImageUrlsFromDatabase(Context context){
		List<String> favoriteList = new ArrayList<String>();
		
	       DatabaseHandler db = new DatabaseHandler(context);
		List<Favorite> favorites = db.getAllFavorites();       
		 
	        for (Favorite f : favorites) {
	            String log = "Id: "+f.getID()+" ,URL: " + f.getUrl() ;
	            favoriteList.add(f.getUrl());
	        }
	        return favoriteList;
	 
	}

	private void writeFile() throws FileNotFoundException {

		FileOutputStream fos = getActivity().getApplicationContext()
				.openFileOutput(fileName, Context.MODE_PRIVATE);

		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(fos)));

		pw.println("http://i.imgur.com/YKkkTvZ.jpg");
		pw.println("http://i.imgur.com/vYaBTpr.jpg");

		pw.close();

	}

	private String[] readFile() throws IOException {

		FileInputStream fis = getActivity().getApplicationContext()
				.openFileInput(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = "";
		String[] favoriteUrls = new String[2];
		int i = 0;
		while (null != (line = br.readLine())) {
			favoriteUrls[i] = line;
			i++;

		}
		br.close();
		return favoriteUrls;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AnimateFirstDisplayListener.displayedImages.clear();
	}

	private static class ViewHolder {
		TextView text;
		ImageView image;
	}

	class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = inflater
						.inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				// holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// holder.text.setText("Item " + (position + 1));

			ImageLoader.getInstance().displayImage(imageUrls.get(position),
					holder.image, options, animateFirstListener);

			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	public void onPauseFragment(Context context) {
		// TODO Auto-generated method stub
		System.out.println("::Fragment Pause");
	}

	@Override
	public void onResumeFragment(Context context) {
		// TODO Auto-generated method stub
		System.out.println("::Fragment Resume");
		List<String> favUrls = new ArrayList<String>();
		favUrls = getFavoriteImageUrlsFromDatabase(context);
		imageUrls = new ArrayList<String>();
		imageUrls.addAll(favUrls);
		//this.mAdapter.notifyDataSetChanged();
		if(mAdapter != null){
		mAdapter.notifyDataSetChanged();
		}
		System.out.println("::Dataset Chaged::");
	}

	
	
	/*@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mAdapter.notifyDataSetChanged();
	}
*/

}