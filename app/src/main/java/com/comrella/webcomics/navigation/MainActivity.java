/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comrella.webcomics.navigation;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.comrella.webcomics.R;
import com.comrella.webcomics.fragment.FavoritesImageListFragment;
import com.comrella.webcomics.fragment.ImageListFragment;
import com.comrella.webcomics.utility.FragmentLifecycle;
import com.mirko.tbv.TabBarView;
import com.mirko.tbv.TabBarView.IconTabProvider;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	public static final String[] iconImages = new String[]{"cheese_selected","fav_selected"};
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private TabBarView tabBarView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkpink));
        }

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        
      //Custom tab viewer
      		LayoutInflater inflator =
      				(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      		View v = inflator.inflate(R.layout.custom_ab, null);
      		tabBarView = (TabBarView) v.findViewById(R.id.tab_bar);

      		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
      		getActionBar().setCustomView(v);
      		
        // Set up the action bar.

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
      		mViewPager = (ViewPager) findViewById(R.id.pager);
    		mViewPager.setAdapter(mAppSectionsPagerAdapter);

    		mViewPager.setOnPageChangeListener(pageChangeListener);
    		
    		tabBarView.setViewPager(mViewPager);
        

    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Get Cheesy Funny Comic Strips on your Android Device. Find us on Play Store! www.comrella.com");
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_app_title)));
		}
		return super.onOptionsItemSelected(item);
	}

    
private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		int currentPosition = 0;
		
		@Override
		public void onPageSelected(int newPosition) {
			getActionBar().setSelectedNavigationItem(newPosition);
			FragmentLifecycle fragmentToHide = (FragmentLifecycle)mAppSectionsPagerAdapter.getItem(currentPosition);
			fragmentToHide.onPauseFragment(getApplicationContext());

			FragmentLifecycle fragmentToShow = (FragmentLifecycle)mAppSectionsPagerAdapter.getItem(newPosition);
			fragmentToShow.onResumeFragment(getApplicationContext());
			
			currentPosition = newPosition;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) { }
		
		public void onPageScrollStateChanged(int arg0) { }
	};


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
       /* int currentPosition = 0 ;
        FragmentLifecycle fragmentToHide = (FragmentLifecycle)mAppSectionsPagerAdapter.getItem(currentPosition);
		fragmentToHide.onPauseFragment(getApplicationContext());

		FragmentLifecycle fragmentToShow = (FragmentLifecycle)mAppSectionsPagerAdapter.getItem(tab.getPosition());
		fragmentToShow.onResumeFragment(getApplicationContext());
		
		currentPosition = tab.getPosition();*/
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {
    	
    	private int[] tab_icons={R.drawable.activity_bar_cheese_x,
				R.drawable.activity_bar_heart_x,
				
		};
    	
    	private String[] tab_icons_description = {"Daily Cheese","Favorite Cheese"};
    	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                	if(getRegisteredFragment(i) != null){
                    	return getRegisteredFragment(i);
                    	}else{
                    	return new ImageListFragment();	
                    	}
                    
                case 1:
                	if(getRegisteredFragment(i) != null){
                	return getRegisteredFragment(i);
                	}else{
                	return new FavoritesImageListFragment();	
                	}
                	

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_icons_description[position];
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
        
        @Override
		public int getPageIconResId(int position) {
			// TODO Auto-generated method stub
			return tab_icons[position];
		}
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);



            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
