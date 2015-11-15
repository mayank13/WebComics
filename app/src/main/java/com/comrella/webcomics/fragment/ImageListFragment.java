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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.comrella.webcomics.R;
import com.comrella.webcomics.database.DatabaseHandler;
import com.comrella.webcomics.database.Favorite;
import com.comrella.webcomics.utility.Constants;
import com.comrella.webcomics.utility.FragmentLifecycle;
import com.comrella.webcomics.utility.NotifyDataUpdateListener;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageListFragment extends AbsEndlessListViewBaseFragment implements
		FragmentLifecycle, NotifyDataUpdateListener {

	public static final int INDEX = 0;

	List<String> imageUrls;
	ImageAdapter mAdapter;
	DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageUrls = new ArrayList<String>();
		try{
			imageUrls.addAll(Constants.IMAGES);
		}catch(Exception e){

		}

		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
		Constants.setDataUpdateListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_list, container,
				false);
		listView = (ListView) rootView.findViewById(android.R.id.list);

		// Adding the load more

		// LoadMore button
		Button btnLoadMore = new Button(getActivity().getApplicationContext());
		btnLoadMore.setText("Load More");

		// Adding Load More button to lisview at bottom
		listView.addFooterView(btnLoadMore);

		/**
		 * Listening to Load More button click event
		 * */
		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Starting a new async task
				Constants.getUrlFromRedditService();
			}
		});
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		AnimateFirstDisplayListener.displayedImages.clear();
	}

	private static class ViewHolder {
		ImageButton favoriteButton;
		ImageButton shareButton;
		ImageView image;
		ImageButton fbShareButton;
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
				holder.favoriteButton = (ImageButton) view
						.findViewById(R.id.FavoriteButton);
				holder.shareButton = (ImageButton) view
						.findViewById(R.id.ShareButton);
				//FB
				holder.fbShareButton = (ImageButton)view.findViewById(R.id.FBShareButton);
				holder.image = (ImageView) view.findViewById(R.id.image);
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// Adding tag to button to get the postion
			holder.favoriteButton.setTag(position);
			holder.shareButton.setTag(position);
			holder.fbShareButton.setTag(position);

			/*ShareLinkContent content = new ShareLinkContent.Builder()
					.setContentUrl(Uri.parse("https://developers.facebook.com"))
					.build();*/
		/*	holder.fbShareButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					File file;
					file = DiskCacheUtils.findInCache(
							imageUrls.get((Integer) v.getTag()),
							ImageLoader.getInstance().getDiscCache());
					int duration = Toast.LENGTH_SHORT;

					Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
					SharePhoto photo = new SharePhoto.Builder()
							.setBitmap(image)
							.build();
					SharePhotoContent content = new SharePhotoContent.Builder()
							.addPhoto(photo)
							.build();


					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), file
							.getAbsolutePath(), duration);
					toast.show();
					holder.fbShareButton.setShareContent(content);
				}
			});*/
			//holder.fbShareButton.setShareContent(content);
			holder.favoriteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Context context = getActivity().getApplicationContext();
					System.out.println("Button tag:" + v.getTag());
					int text = (Integer) v.getTag();
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context,
							"Added to favorites:" + imageUrls.get(text),
							duration);
					DatabaseHandler db = new DatabaseHandler(getActivity()
							.getApplicationContext());

					// Inserting Contacts
					Log.d("Insert: ", "Inserting ..");
					db.addFavorite(new Favorite(imageUrls.get(text)));
					toast.show();
				}
			});

			holder.shareButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					File file;
					file = DiskCacheUtils.findInCache(
							imageUrls.get((Integer) v.getTag()),
							ImageLoader.getInstance().getDiscCache());
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), file
							.getAbsolutePath(), duration);
					toast.show();
					Uri uri = Uri.parse(file.getAbsolutePath());

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT,
							"Sent via Comrella ! Find us on Play Store . www.comrella.com");
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_STREAM, uri);
					intent.setType("image/jpeg");
					intent.setPackage("com.whatsapp");
					startActivity(intent);
				}
			});

			//Facebook Share Button

			holder.fbShareButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					File file;
					file = DiskCacheUtils.findInCache(
							imageUrls.get((Integer) v.getTag()),
							ImageLoader.getInstance().getDiscCache());
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), file
							.getAbsolutePath(), duration);
					toast.show();
					Uri uri = Uri.parse(file.getAbsolutePath());

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					/*intent.putExtra(Intent.EXTRA_TEXT,
							"Sent via Comrella ! Find us on Play Store . www.comrella.com");
					intent.setType("text/plain");*/
					intent.putExtra(Intent.EXTRA_STREAM, uri);
					intent.setType("image/jpeg");
					intent.setPackage("com.facebook.katana");
					startActivity(intent);
				}
			});

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

	}

	@Override
	public void onResumeFragment(Context context) {
		// TODO Auto-generated method stub
		// imageUrls.add("http://api.androidhive.info/json/movies/1.jpg");
		// mAdapter.notifyDataSetChanged();

	}

	@Override
	public void dataUpdated(List<String> data) {
		// TODO Auto-generated method stub
		System.out.println("::Notify data update listener called");
		System.out.println("Looper "+Looper.myLooper());
		System.out.println("Main Looper "+Looper.getMainLooper());
		imageUrls.addAll(data);
		mAdapter.notifyDataSetChanged();
	}
}