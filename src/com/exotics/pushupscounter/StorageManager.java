package com.exotics.pushupscounter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

@SuppressWarnings("static-access")
public class StorageManager {

	// method to save High Score value
	public void saveHighScore(String name, int value, Context context) {
		// Saving the High Score value to the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the High Score value under the name of "Saved 'String' name"
		editor.putInt("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// method to save Calories value
	public void saveCalories(String name, int value, Context context) {
		// Saving the Calories value to the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the Calories value under the name of "Saved 'String' name"
		editor.putInt("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// method to save Timer value
	public void saveTimer(String name, String value, Context context) {
		// Saving the Timer value to the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the Timer value under the name of "Saved 'String' name"
		editor.putString("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// Method to save the sensor type to use
	public void savedSensorType(int value, String name, Context context) {
		// Saving the sensor type to the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the CheckBox value under the name of "Saved 'String' name"
		editor.putInt("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// method to save counter value
	public void saveCounterValue(int value, Context context) {
		// Saving the counter value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(
				"counterValue", context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the counter value under the name of "savedCounterValue"
		editor.putInt("savedCounterValue", value);
		// Commit the action and save the value
		editor.commit();
	}

	// Method to save the Don't show CheckBox status
	public void saveDontShowHelp(boolean value, String name, Context context) {
		// Saving the CheckBox value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the CheckBox value under the name of "Saved 'String' name"
		editor.putBoolean("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// Method to save the WakeLock CheckBox status
	public void saveWakeLockValue(boolean value, String name, Context context) {
		// Saving the CheckBox value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		// Make editor object to write to the shared preferences
		SharedPreferences.Editor editor = myPrefs.edit();
		// Saving the CheckBox value under the name of "Saved 'String' name"
		editor.putBoolean("Saved" + name, value);
		// Commit the action and save the value
		editor.commit();
	}

	// method to save counter value to logs
	public void savelogs(String value, Context context) {
		File file = context.getFileStreamPath("logs.txt"); // Get default file
															// path

		try {
			if (!file.exists()) { // File doesn't exists create the file
				file.createNewFile();
			}

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					file, true)); // New file reader with append = true

			// Pass counter value and date to writer
			bufferedWriter.append(value);
			bufferedWriter.close(); // Close buffer writer
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// method to save counter value to logs
	public void saveHistory(String value, Context context) {
		File file = context.getFileStreamPath("History.txt"); // Get default
																// file path

		try {
			if (!file.exists()) { // File doesn't exists create the file
				file.createNewFile();
			}

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					file, true)); // New file reader with append = true

			// Pass counter value and date to writer
			bufferedWriter.append(value);
			bufferedWriter.close(); // Close buffer writer
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method to get counter saved value
	public int getHighScore(String name, Context context) {
		// Retrieving the counter value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		int Value = myPrefs.getInt("Saved" + name, 0);
		return Value;
	}

	// method to get Calories saved value
	public int getCalories(String name, Context context) {
		// Retrieving the Calories value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		int Value = myPrefs.getInt("Saved" + name, 0);
		return Value;
	}

	// method to get Timer saved value
	public String getTimer(String name, Context context) {
		// Retrieving the Timer value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		String Value = myPrefs.getString("Saved" + name, "00:00");
		return Value;
	}

	// method to get counter saved value
	public int getCounterValue(Context context) {
		// Retrieving the counter value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(
				"counterValue", context.MODE_PRIVATE);
		int Value = myPrefs.getInt("savedCounterValue", 0);
		return Value;
	}

	// Method to get the sensor type
	public int getSensorType(String name, Context context) {
		// Retrieving the sensor type from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		int SensorType = myPrefs.getInt("Saved" + name, 1);
		return SensorType;
	}

	// Method to get the WakeLock CheckBox status
	public boolean getWakeLockValue(String name, Context context) {
		// Retrieving the CheckBox value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		boolean CheckBoxstatus = myPrefs.getBoolean("Saved" + name, true);
		return CheckBoxstatus;
	}

	// Method to get Don't show CheckBox status
	public boolean getDontShowHelp(String name, Context context) {
		// Retrieving the CheckBox value from the shared preferences
		SharedPreferences myPrefs = context.getSharedPreferences(name,
				context.MODE_PRIVATE);
		boolean CheckBoxstatus = myPrefs.getBoolean("Saved" + name, false);
		return CheckBoxstatus;
	}

	// method to get counter saved value from history
	public String getLogs(Context context) {
		File file = context.getFileStreamPath("logs.txt"); // Get default file
															// path
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file)); // New file reader

			String read;
			// String builder to read huge string
			StringBuilder builder = new StringBuilder("");

			// While there is text read line
			while ((read = bufferedReader.readLine()) != null) {
				builder.append(read + "\n\n");
			}
			bufferedReader.close(); // Close buffer writer
			return builder.toString();
		} catch (Exception e) {
			return "No Log!";
		}
	}

	@SuppressLint("SdCardPath")
	public void clearAppData(Context context) {
		File file = context.getFileStreamPath("logs.txt"); // Get file path
		file.delete(); // Delete log File
		file = context.getFileStreamPath("History.txt"); // Get file path
		file.delete(); // Delete log File

		// Cache Folder
		File dir = context.getCacheDir();
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				new File(dir, children[i]).delete();
			}
		}
		// Shared Prefs Folder
		dir = new File("/data/data/com.exotics.pushupscounter/shared_prefs/");
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				new File(dir, children[i]).delete();
			}
		}

		Toast.makeText(context, "Cleared!", Toast.LENGTH_SHORT).show();
		
		//Close and restart the application
		Intent mStartActivity = new Intent(context, MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(context,
				mPendingIntentId, mStartActivity,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
				mPendingIntent);
		System.exit(0);
	}

	// method to get counter saved value from history
	public void getDailyHighScore(Context context) {
		File file = context.getFileStreamPath("History.txt"); // Get default
																// file path
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file)); // New file reader

			int highScore = 0;
			int pushUps[] = new int[30];
			int calories[] = new int[30];

			boolean oneTime = true;
			String read = ""; // Read line by line
			String count = ""; // Read push ups count
			String currentDay = ""; // Read the current day of the log
			String PrevMonth = ""; // Read the month of the log
			String currentMonth = ""; // Read the month of the log
			String PrevYear = ""; // Read the year of the log
			String currentYear = ""; // Read the year of the log

			// While there is text read line
			while ((read = bufferedReader.readLine()) != null) {

				count = (String) read.subSequence(0, read.indexOf(","));
				currentDay = (String) read.subSequence(read.indexOf(",") + 1,
						read.indexOf("-"));
				currentMonth = (String) read.subSequence(read.indexOf("-") + 1,
						read.indexOf("/"));
				currentYear = (String) read.substring(read.indexOf("/") + 1);

				if (oneTime) {
					PrevMonth = currentMonth;
					PrevYear = currentYear;
					oneTime = false;
				}

				if ((currentMonth.equals(PrevMonth) && currentYear
						.equals(PrevYear))) {
					pushUps[Integer.valueOf(currentDay) - 1] += Integer
							.valueOf(count);
					calories[Integer.valueOf(currentDay) - 1] += (Integer
							.valueOf(count)) * 0.825; // calories
					for (int i = 0; i < pushUps.length; i++) {
						highScore = pushUps[i];
						if (getHighScore("DailyScore", context) < highScore) {
							saveHighScore("DailyScore", highScore, context);
							saveCalories("DailyCalories",
									(int) (highScore * 0.825), context);
						}
					}
				} else {
					Arrays.fill(pushUps, 0); // Resets the array
					Arrays.fill(calories, 0); // Resets the array
					pushUps[Integer.valueOf(currentDay) - 1] += Integer
							.valueOf(count);
					calories[Integer.valueOf(currentDay) - 1] += (Integer
							.valueOf(count)) * 0.825; // calorie
					PrevMonth = currentMonth;
					PrevYear = currentYear;
				}

			}
			bufferedReader.close(); // Close buffer writer
		} catch (Exception e) {
		}
	}

	public boolean compareTimes(String currentTime, String prevTime) {
		int currentMinutes = 0;
		int currentSeconds = 0;
		int prevMinutes = 0;
		int prevSeconds = 0;

		if (currentTime != null && prevTime != null) {
			// 00:00
			currentMinutes = Integer.valueOf((String) currentTime.subSequence(
					0, 1));
			currentSeconds = Integer.valueOf((String) currentTime.subSequence(
					3, 4));

			prevMinutes = Integer.valueOf((String) prevTime.subSequence(0, 1));
			prevSeconds = Integer.valueOf((String) prevTime.subSequence(3, 4));

			if (currentMinutes > prevMinutes) {
				return true;
			} else {
				if (currentSeconds > prevSeconds) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
