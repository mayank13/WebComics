package com.comrella.webcomics.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.comrella.webcomics.R;


public class SplashScreen extends Activity implements NavigateToScreen {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Splash::onCreate");
		com.comrella.webcomics.utility.Constants.setmTheListener(this);
		com.comrella.webcomics.utility.Constants.getUrlFromRedditService();
		setContentView(R.layout.activity_splash_screen);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("Splash::onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Splash::onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("Splash::onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("Splash::onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("Splash::On onDestroy");
	}

	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("Splash::On Start - Splash");
	}

	@Override
	public void GotoNextScreen() {
		// TODO Auto-generated method stub
		System.out.println("i am Called");
		Intent i = new Intent(SplashScreen.this, MainActivity.class);
		startActivity(i);

		// close this activity
		finish();
	}
}
