package com.exotics.pushupscounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Settings extends SherlockActivity {

	private StorageManager myManager = new StorageManager();

	// Defined Array values to show in ListView
	String[] values = new String[] { "Clear App Data" };

	private PowerManager myPowermanager;
	private PowerManager.WakeLock CPUWL;
	private AudioManager audioManager;
	private CheckBox wakeLockCheckbox;
	private ListView listView;

	@SuppressLint("Wakelock")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// ActionBar
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.GRAY));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		myPowermanager = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		CPUWL = myPowermanager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"CPU_WAKE_LOCK");

		// Get ListView object from xml
		listView = (ListView) findViewById(R.id.settingsList);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;

				switch (itemPosition) {
				case 0:
					myManager.clearAppData(getApplicationContext());
					break;

				default:
					break;
				}
			}

		});

		wakeLockCheckbox = (CheckBox) findViewById(R.id.wakeLockCheckBox);
		wakeLockCheckbox.setChecked(myManager.getWakeLockValue("screenLock",
				getApplicationContext()));

		wakeLockCheckbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wakeLockCheckbox.isChecked() == true) {
					wakeLockCheckbox.setChecked(false);
					myManager.saveWakeLockValue(false, "screenLock",
							getApplicationContext());
					if (CPUWL.isHeld() == true)
						CPUWL.release();
				} else {
					wakeLockCheckbox.setChecked(true);
					myManager.saveWakeLockValue(true, "screenLock",
							getApplicationContext());
					if (CPUWL.isHeld() == false)
						CPUWL.acquire();
				}
			}
		});

		// Volume Slider
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		SeekBar volControl = (SeekBar) findViewById(R.id.volumeSlider);
		volControl.setMax(maxVolume);
		volControl.setProgress(curVolume);
		volControl
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								arg1, 0);
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@SuppressLint("Wakelock")
	private void screenLockChecker() {
		if (myManager.getWakeLockValue("screenLock", getApplicationContext()) == true) {
			if (CPUWL.isHeld() == false)
				CPUWL.acquire();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		screenLockChecker();
	}
}
