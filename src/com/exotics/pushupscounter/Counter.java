package com.exotics.pushupscounter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Counter extends Activity {
	private StorageManager myManager = new StorageManager();
	private SoundPool spool; // Make soundPool object called "spool"
	private PowerManager myPowermanager;
	private PowerManager.WakeLock CPUWL;
	private int upClick;
	private int downClick;
	private int reset;
	private int lightThreshold;
	private SensorManager mySensorManager;
	private Sensor ProximitySensor;
	private View touchView;

	// *****************//
	// Timer Variables //
	// *****************//
	private boolean TIMERRUNNING = true;
	private TextView textTimer;
	private long startTime = 0L;
	private Handler myHandler = new Handler();
	long timeInMillies = 0L;
	long timeSwap = 0L;
	long finalTime = 0L;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// No Title Bar
		setContentView(R.layout.activity_counter);

		// Ad View
		AdView adView = new AdView(this, AdSize.BANNER,
				"ca-app-pub-3215450594684581/6184968550");
		LinearLayout ll = (LinearLayout) findViewById(R.id.adView);
		ll.addView(adView);
		AdRequest ar = new AdRequest();
		ar.setGender(AdRequest.Gender.UNKNOWN);
		adView.loadAd(ar);
		// Ad View

		myPowermanager = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		CPUWL = myPowermanager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"CPU_WAKE_LOCK");

		textTimer = (TextView) findViewById(R.id.timer);
		startTime = SystemClock.uptimeMillis();
		myHandler.postDelayed(updateTimerMethod, 0);

		TextView highScore = (TextView) findViewById(R.id.highscore);
		highScore.append("\t"
				+ String.valueOf(myManager.getHighScore("overallScore",
						getApplicationContext())));

		// Use the audio manger and import the audio files
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		/*
		 * make a new soundPool object with maximum 10 sound flies and 100%
		 * sound quality that use audio manger API to stream the sound file
		 */
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		// Loading the upClick, downClick and reset button sound into the
		// soundPool object spool
		upClick = spool.load(this, R.raw.upclick, upClick);
		downClick = spool.load(this, R.raw.downclick, downClick);
		reset = spool.load(this, R.raw.reset, reset);

		// Handles single touch events
		touchView = findViewById(R.id.touchView);
		touchView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				if ((action == MotionEvent.ACTION_DOWN)
						&& (action != MotionEvent.ACTION_POINTER_DOWN)) {
					countUp();
				}

				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.counter, menu);
		return true;
	}

	private final SensorEventListener LightSensorListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
				switch (myManager.getSensorType("sensorType",
						getApplicationContext())) {
				case 1:
					proximityCounter(event);
					break;
				case 2:
					lightCounter(event);
					break;
				default:
					break;
				}
			}
		}

	};

	private void lightCounter(SensorEvent event) {
		if (event.values[0] >= 500) {
			lightThreshold = 16;
		} else if (event.values[0] >= 95) {
			lightThreshold = 11;
		} else if (event.values[0] <= 10) {
			lightThreshold = 3;
		}
		if (event.values[0] <= lightThreshold) {
			countUp();
		}
	}

	private void proximityCounter(SensorEvent event) {
		if (event.values[0] == 0) {
			countUp();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_stayAwake).setChecked(
				myManager.getWakeLockValue("screenLock",
						getApplicationContext()));

		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressLint("Wakelock")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		// If reset count item selected do that
		case R.id.menu_resetButton:
			resetCounter();
			playResetClick();
			return true;
		case R.id.menu_settings:
			// Start Settings activity
			Intent settingsIntent = new Intent(this, Settings.class);
			startActivity(settingsIntent);
			return true;
			// If help item selected do that
		case R.id.menu_help:
			// Start Help activity
			Intent helpIntent = new Intent(this, Help.class);
			startActivity(helpIntent);
			return true;
		case R.id.menu_stayAwake:
			// Screen Wake Lock CheckBox
			if (item.isChecked() == true) {
				item.setChecked(false);
				myManager.saveWakeLockValue(false, "screenLock",
						getApplicationContext());
				if (CPUWL.isHeld() == true)
					CPUWL.release();
			} else {
				item.setChecked(true);
				myManager.saveWakeLockValue(true, "screenLock",
						getApplicationContext());
				if (CPUWL.isHeld() == false)
					CPUWL.acquire();
			}

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("Wakelock")
	private void screenLockChecker() {
		if (myManager.getWakeLockValue("screenLock", getApplicationContext()) == true) {
			if (CPUWL.isHeld() == false)
				CPUWL.acquire();
		}
	}

	// Trace the volume buttons actions
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();

		switch (keyCode) {
		// When clicking the upper volume button
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
				countUp();
			}
			return true;

			// When clicking the down volume button
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				countDown();
			}
			return true;

		default:
			return super.dispatchKeyEvent(event);// Releasing volume buttons
		}
	}

	private void countUp() {
		// play up click sound
		playUpClick();

		// Get text view from the xml file
		TextView countView = (TextView) findViewById(R.id.text_count);

		// Get the text from the text view file in the xml and
		// convert
		// it to string
		String toString = countView.getText().toString();
		// Converting the String value to int value
		int toInt = Integer.parseInt(toString);
		toInt++;
		// Converting int value to String value to add to the text
		// view
		toString = String.valueOf(toInt);
		countView.setText(toString);// text views only take string
									// values

		myManager.saveCounterValue(toInt, getApplicationContext());// call
																	// saveCounter
																	// method
																	// and give
		// the
		// value toInt
	}

	private void countDown() {
		// play down click sound
		playDownClick();

		// Get text view from the xml file
		TextView countView = (TextView) findViewById(R.id.text_count);

		// Get the text from the text view file in the xml and convert
		// it to string
		String toString = countView.getText().toString();
		// Converting the String value to int value
		int toInt = Integer.parseInt(toString);
		toInt--;
		// Converting int value to String value to add to the text view
		toString = String.valueOf(toInt);
		countView.setText(toString);// text views only take string
									// values

		myManager.saveCounterValue(toInt, getApplicationContext());// call
																	// saveCounter
																	// method
																	// and give
		// the
		// value toInt
	}

	// rest the counter value on click
	private void resetCounter() {
		TextView countView = (TextView) findViewById(R.id.text_count);
		// Set counter value to 0
		countView.setText("0");
		myManager.saveCounterValue(0, getApplicationContext());

		Pause();
		TextView time = (TextView) findViewById(R.id.timer);
		// Set timer value to default
		time.setText("00:00:00");
		// Rest Time
		startTime = SystemClock.uptimeMillis();
		timeInMillies = 0L;
		timeSwap = 0L;
		finalTime = 0L;
	}

	// up click sound method
	private void playUpClick() {
		// using the audio manger API
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		// Setting the volume value as a float value
		float volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		/*
		 * play the "upClick" sound with volume value on left and right with
		 * priority value "1" and looping 0 times and setting the float value to
		 * one decimal point
		 */
		spool.play(upClick, volume, volume, 1, 0, 1f);
	}

	// down click sound method
	private void playDownClick() {
		// using the audio manger API
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		// Setting the volume value as a float value
		float volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		/*
		 * play the "upClick" sound with volume value on left and right with
		 * priority value "1" and looping 0 times and setting the float value to
		 * one decimal point
		 */
		spool.play(downClick, volume, volume, 1, 0, 1f);
	}

	// reset click sound method
	private void playResetClick() {
		// using the audio manger API
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		// Setting the volume value as a float value
		float volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		/*
		 * play the "reset" sound with volume value on left and right with
		 * priority value "1" and looping 0 times and setting the float value to
		 * one decimal point
		 */
		spool.play(reset, volume, volume, 1, 0, 1f);
	}

	private void restartListeners() {
		// Sensor LightSensor = mySensorManager
		// .getDefaultSensor(Sensor.TYPE_LIGHT);
		ProximitySensor = mySensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		if (ProximitySensor != null) {
			mySensorManager.registerListener(LightSensorListener,
					ProximitySensor, SensorManager.SENSOR_DELAY_UI);
		}

		touchView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				if ((action == MotionEvent.ACTION_DOWN)
						&& (action != MotionEvent.ACTION_POINTER_DOWN)) {
					countUp();
				}

				return true;
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	public void Done(View view) {
		// Get text view from the xml file
		TextView countView = (TextView) findViewById(R.id.text_count);

		// Get the text from the text view file in the xml and
		// convert
		// it to string
		String toString = countView.getText().toString();

		if (myManager.getHighScore("overallScore", getApplicationContext()) < Integer
				.valueOf(countView.getText().toString())) {
			int pushUps = Integer.valueOf(countView.getText().toString());
			int calories = (int) (pushUps * 0.825);
			myManager.saveHighScore("overallScore", pushUps,
					getApplicationContext());
			myManager.saveCalories("overallCalories", calories,
					getApplicationContext());
		}

		String currentTime = (String) textTimer.getText();
		String prevTime = myManager.getTimer("Timer", getApplicationContext());
		if (myManager.compareTimes(currentTime, prevTime)) {
			myManager.saveTimer("Timer", currentTime, getApplicationContext());
		}

		// Get current date and time
		SimpleDateFormat logDateFormat = new SimpleDateFormat("dd MMM yyyy",
				Locale.US);
		SimpleDateFormat historyDateFormat = new SimpleDateFormat("dd-MM/yyyy",
				Locale.US);
		Date date = new Date();

		// Date and counter value
		String logsText = (logDateFormat.format(date) + ": " + " You did "
				+ toString + " push ups\n");

		// Date and counter value
		String historyText = (toString + "," + historyDateFormat.format(date) + "\n");
		// Save to Logs
		myManager.savelogs(logsText, getApplicationContext());
		// Save to History
		myManager.saveHistory(historyText, getApplicationContext());
		resetCounter();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void Pause() {
		if (TIMERRUNNING) {
			timeSwap += timeInMillies;
			myHandler.removeCallbacks(updateTimerMethod);
			mySensorManager.unregisterListener(LightSensorListener);
			touchView.setOnTouchListener(null);
			TIMERRUNNING = false;
			Toast.makeText(getApplicationContext(), "Paused!", Toast.LENGTH_SHORT).show();
		}
	}

	private void PlayPause() {
		if (TIMERRUNNING) {
			timeSwap += timeInMillies;
			myHandler.removeCallbacks(updateTimerMethod);
			mySensorManager.unregisterListener(LightSensorListener);
			touchView.setOnTouchListener(null);
			TIMERRUNNING = false;
		} else {
			startTime = SystemClock.uptimeMillis();
			myHandler.postDelayed(updateTimerMethod, 0);
			restartListeners();
			TIMERRUNNING = true;
		}
	}

	public void PlayPause(View view) {
		PlayPause();
	}

	@Override
	public void onResume() {
		super.onResume();// Always call the super.onResume(); when overriding
							// onResume() method

		screenLockChecker();

		// Light Sensor Code
		mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Sensor LightSensor = mySensorManager
		// .getDefaultSensor(Sensor.TYPE_LIGHT);
		ProximitySensor = mySensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		if (ProximitySensor != null) {
			mySensorManager.registerListener(LightSensorListener,
					ProximitySensor, SensorManager.SENSOR_DELAY_UI);
		}

		// Retrieving the count value from the getCounterValue() method and
		// convert it to string
		String toString = String.valueOf(myManager
				.getCounterValue(getApplicationContext()));
		TextView countView = (TextView) findViewById(R.id.text_count);
		countView.setText(toString);
	}

	@Override()
	public void onPause() {
		super.onPause();
		if (CPUWL.isHeld() == true)
			CPUWL.release();
		mySensorManager.unregisterListener(LightSensorListener);
	}

	private Runnable updateTimerMethod = new Runnable() {

		public void run() {
			timeInMillies = SystemClock.uptimeMillis() - startTime;
			finalTime = timeSwap + timeInMillies;

			int seconds = (int) (finalTime / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			int milliseconds = (int) (finalTime % 1000);
			textTimer.setText("" + minutes + ":"
					+ String.format("%02d", seconds) + ":"
					+ String.format("%03d", milliseconds));
			myHandler.postDelayed(this, 0);
		}

	};

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
