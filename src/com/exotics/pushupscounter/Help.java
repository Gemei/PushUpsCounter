package com.exotics.pushupscounter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Help extends SherlockActivity {
	private StorageManager myManager = new StorageManager();
	private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// ActionBar
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.GRAY));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		checkBox = (CheckBox) findViewById(R.id.helpCheckbox);
		checkBox.setChecked(myManager.getDontShowHelp("DontShow",
				getApplicationContext()));
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

	public void helpDone(View view) {
		// Is the view now checked?
		checkBox = (CheckBox) findViewById(R.id.helpCheckbox);
		myManager.saveDontShowHelp(checkBox.isChecked(), "DontShow",
				getApplicationContext());
		finish();
	}
}
