package com.exotics.pushupscounter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends SherlockActivity implements
		ISideNavigationCallback {

	private StorageManager myManager = new StorageManager();

	private PowerManager myPowermanager;
	private PowerManager.WakeLock CPUWL;

	private SideNavigationView sideNavigationView;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Ad View
		AdView adView = new AdView(this, AdSize.BANNER,
				"ca-app-pub-3215450594684581/6184968550");
		LinearLayout ll = (LinearLayout) findViewById(R.id.adView);
		ll.addView(adView);
		AdRequest ar = new AdRequest();
		ar.setGender(AdRequest.Gender.UNKNOWN);
		adView.loadAd(ar);
		// Ad View

		// Side Menu
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.main);
		sideNavigationView.setMenuClickCallback(this);

		// ActionBar
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.GRAY));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (myManager.getDontShowHelp("DontShow", getApplicationContext()) == false) {
			// Start Help activity
			Intent helpIntent = new Intent(this, Help.class);
			startActivity(helpIntent);
		}

		myPowermanager = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		CPUWL = myPowermanager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"CPU_WAKE_LOCK");

		PackageManager PM = this.getPackageManager();
		boolean LIGHT = PM
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
		boolean PROXIMITY = PM
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
		if (PROXIMITY == false) {
			if (LIGHT != false) {
				myManager.savedSensorType(2, "sensorType",
						getApplicationContext());
				lightSwitchDialog();
			} else {
				notWorkDialog();
			}
		}

		showHighScores();
		TextView time = (TextView) findViewById(R.id.TimeText);
		time.append(myManager.getTimer("Timer", getApplicationContext()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	@SuppressLint("Wakelock")
	public void onSideNavigationItemClick(int itemId) {
		// Handle item selection
		switch (itemId) {
		case R.id.menu_history:
			// Start logs activity
			Intent historyIntent = new Intent(this, History.class);
			startActivity(historyIntent);
			break;
		case R.id.menu_logs:
			// Start logs activity
			Intent logsIntent = new Intent(this, Logs.class);
			startActivity(logsIntent);
			break;
		// If help item selected do that
		case R.id.menu_about:
			// Start About activity
			Intent aboutIntent = new Intent(this, About.class);
			startActivity(aboutIntent);
			break;
		case R.id.menu_help:
			// Start Help activity
			Intent helpIntent = new Intent(this, Help.class);
			startActivity(helpIntent);
			break;
		case R.id.menu_settings:
			// Start Help activity
			Intent settingsIntent = new Intent(this, Settings.class);
			startActivity(settingsIntent);
			finish();
			break;
		default:
			break;
		}
	}

	public void startWorkout(View view) {
		Intent intent = new Intent(this, Counter.class);
		startActivity(intent);
		finish();
	}

	@SuppressLint("Wakelock")
	private void screenLockChecker() {
		if (myManager.getWakeLockValue("screenLock", getApplicationContext()) == true) {
			if (CPUWL.isHeld() == false)
				CPUWL.acquire();
		}
	}

	public void lightSwitchDialog() {

		AlertDialog.Builder lightSwitch = new AlertDialog.Builder(this);
		lightSwitch.setTitle("Switch To light Sensor");
		TextView myMsg = new TextView(this);
		myMsg.setTextSize(19);
		myMsg.setText("Proximity sensor is not available. Switching to light sensor");
		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

		lightSwitch.setView(myMsg).setPositiveButton("Ok",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				});

		lightSwitch.show();
	}

	public void notWorkDialog() {

		AlertDialog.Builder lightSwitch = new AlertDialog.Builder(this);
		lightSwitch.setTitle("No Sensore Available!");
		TextView myMsg = new TextView(this);
		myMsg.setTextSize(19);
		myMsg.setText("Proximity sensor and light sensor are not available, sorry but the application is not going to work for you");
		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

		lightSwitch.setView(myMsg).setPositiveButton("Ok",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				});

		lightSwitch.show();
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			// Side Navigation Menu
			sideNavigationView.toggleMenu();
			return true;
		}
		return super.onKeyDown(keycode, e);
	}

	private void showHighScores() {
		myManager.getDailyHighScore(getApplicationContext());
		TextView highScore = (TextView) findViewById(R.id.highScoreText);
		TextView calories = (TextView) findViewById(R.id.caloriesText);
		TextView DailyHighScore = (TextView) findViewById(R.id.DailyHighScoreText);
		TextView DailyCalories = (TextView) findViewById(R.id.DailyCaloriesText);
		String highScoreText = String.valueOf(myManager.getHighScore(
				"overallScore", getApplicationContext()));
		String caloriesText = String.valueOf(myManager.getHighScore(
				"overallCalories", getApplicationContext()));
		String DailyHighScoreText = String.valueOf(myManager.getHighScore(
				"DailyScore", getApplicationContext()));
		String DailyCaloriesText = String.valueOf(myManager.getHighScore(
				"DailyCalories", getApplicationContext()));
		highScore.append(" " + highScoreText);
		calories.append(" " + caloriesText);
		DailyHighScore.append(" " + DailyHighScoreText);
		DailyCalories.append(" " + DailyCaloriesText);
	}

	@Override
	public void onBackPressed() {
		// hide menu if it shown
		if (sideNavigationView.isShown()) {
			sideNavigationView.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onResume() {
		super.onResume();// Always call the super.onResume(); when overriding
							// onResume() method
		screenLockChecker();
	}

	@Override()
	public void onPause() {
		super.onPause();
		if (CPUWL.isHeld() == true)
			CPUWL.release();
	}
}
