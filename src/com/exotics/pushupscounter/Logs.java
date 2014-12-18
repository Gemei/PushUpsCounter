package com.exotics.pushupscounter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Logs extends SherlockActivity {
	private StorageManager myManager = new StorageManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logs);

		// ActionBar
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.GRAY));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView readHistory = (TextView) findViewById(R.id.Logs);

		readHistory.append(myManager.getLogs(getApplicationContext()));
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
}
