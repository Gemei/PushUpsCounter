package com.exotics.pushupscounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class History extends SherlockActivity {

	// Create XYMultipleSeriesRenderer to customize the whole chart
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private GraphicalView mChart;
	private int YEAR = 0;
	private int MONTH = 0;

	// Array with names of all month
	private String[] MonthNames = new String[] { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	private int[] mDay = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 };

	private int pushUps[] = new int[30];
	private int calories[] = new int[30];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// ActionBar
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.GRAY));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Calendar c = new GregorianCalendar();
		YEAR = c.get(Calendar.YEAR); // Get Current Year
		MONTH = c.get(Calendar.MONTH) + 1; // Get Current Month, +1 so the month
											// count starts with 1 rather than 0
		TextView dateTitle = (TextView) findViewById(R.id.dateTitle);
		String monthName = "";
		monthName = MonthNames[MONTH - 1]; // Get equivalent name for the month
											// number
		dateTitle.setText(monthName + ", " + YEAR); // Set month and year

		getHistory(); // Call get history to read the history
		OpenChart(); // Call get OpenChart render the chart
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
	
	private void OpenChart() {

		// Chart Container
		LinearLayout chart_container = (LinearLayout) findViewById(R.id.historyGraph);

		// Create XY Series for X Series.
		XYSeries xSeriesPushups = new XYSeries("Push-ups");
		XYSeries xSeriesCalories = new XYSeries("Calories");

		// Adding data to the X Series.
		for (int i = 0; i < mDay.length; i++) {
			try {
				xSeriesPushups.add(mDay[i], pushUps[i]);
				xSeriesCalories.add(mDay[i], calories[i]);
			} catch (ArrayIndexOutOfBoundsException e) {
				xSeriesPushups.add(mDay[i], 0);
				xSeriesCalories.add(mDay[i], 0);
			}
		}

		// Create a Data set to hold the XSeries.
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		// Add X series to the Data set.
		dataset.addSeries(xSeriesPushups);
		dataset.addSeries(xSeriesCalories);

		// Create XYSeriesRenderer to customize XSeries
		XYSeriesRenderer XrendererPushUps = new XYSeriesRenderer();
		XrendererPushUps.setColor(Color.parseColor("#0080ff"));
		XrendererPushUps.setChartValuesTextSize(16);
		XrendererPushUps.setChartValuesSpacing(10);
		XrendererPushUps.setDisplayChartValues(true);
		XrendererPushUps.setLineWidth(5);
		XrendererPushUps.setFillPoints(true);

		// Create XYSeriesRenderer to customize XSeries
		XYSeriesRenderer XrendererCalories = new XYSeriesRenderer();
		XrendererCalories.setColor(Color.parseColor("#ffa500"));
		XrendererCalories.setChartValuesTextSize(16);
		XrendererCalories.setChartValuesSpacing(10);
		XrendererCalories.setDisplayChartValues(true);
		XrendererCalories.setLineWidth(5);
		XrendererCalories.setFillPoints(true);

		// Create XYMultipleSeriesRenderer to customize the whole chart
		mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.setChartTitle("History");
		mRenderer.setXTitle("Days");
		mRenderer.setYTitle("Push-Ups");
		mRenderer.setXAxisMin(2);
		mRenderer.setXAxisMax(14);
		mRenderer.setFitLegend(false);

		mRenderer.setPanEnabled(true, false);
		mRenderer.setPanLimits(new double[] { 0, 31, 0, 0 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setZoomInLimitX(3);
		mRenderer.setZoomLimits(new double[] { -2, 31, 0, 0 });

		mRenderer.setXLabels(0);
		mRenderer.setShowGrid(true);
		mRenderer.setGridColor(Color.LTGRAY);
		mRenderer.setBarSpacing(0);
		mRenderer.setLabelsTextSize(21);
		mRenderer.setXLabelsPadding(5);
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);

		mRenderer.setClickEnabled(true);

		for (int i = 0; i < mDay.length; i++) {
			mRenderer.addXTextLabel(i + 1, String.valueOf(mDay[i]));
		}

		// Adding the XSeriesRenderer to the MultipleRenderer.
		mRenderer.addSeriesRenderer(XrendererPushUps);
		mRenderer.addSeriesRenderer(XrendererCalories);

		// Creating an intent to plot line chart using data set
		// and multipleRenderer
		mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(),
				dataset, mRenderer, Type.DEFAULT);

		// Adding click event to the Line Chart.
		mChart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				SeriesSelection series_selection = mChart
						.getCurrentSeriesAndPoint();

				if (series_selection != null) {
					int series_index = series_selection.getSeriesIndex();

					int Day = mDay[(int) series_selection.getXValue() - 1];

					if (series_index == 0) {
						int pushUps = (int) series_selection.getValue();
						Toast.makeText(
								getBaseContext(),
								"On day " + Day + " you did " + pushUps
										+ " push ups.", Toast.LENGTH_LONG)
								.show();
					} else if (series_index == 1) {
						int calories = (int) series_selection.getValue();
						Toast.makeText(
								getBaseContext(),
								"On day " + Day + " you burned " + calories
										+ " calories.", Toast.LENGTH_LONG)
								.show();
					}

				}
			}
		});

		// Add the graphical view mChart object into the Linear layout after
		// reseting the layout and repainting the chart.
		chart_container.removeAllViews();
		mChart.repaint();
		chart_container.addView(mChart);
	}

	public void nextMonth(View view) {
		TextView dateTitle = (TextView) findViewById(R.id.dateTitle);
		String monthName = "";
		++MONTH;
		// Make suer the month count will not exceeds it's boundaries
		if (MONTH == 13) {
			YEAR++;
			MONTH = 1;
		}
		monthName = MonthNames[MONTH - 1];
		dateTitle.setText(monthName + ", " + YEAR);
		Arrays.fill(pushUps, 0); // Resets the array
		Arrays.fill(calories, 0); // Resets the array
		getHistory();
		OpenChart();
	}

	public void prevMonth(View view) {
		TextView dateTitle = (TextView) findViewById(R.id.dateTitle);
		String monthName = "";
		--MONTH;
		// Make suer the month count will not exceeds it's boundaries
		if (MONTH == 0) {
			YEAR--;
			MONTH = 12;
		}
		monthName = MonthNames[MONTH - 1];
		dateTitle.setText(monthName + ", " + YEAR);
		Arrays.fill(pushUps, 0); // Resets the array
		Arrays.fill(calories, 0); // Resets the array
		getHistory();
		OpenChart();
	}

	// method to get counter saved value from history
	private void getHistory() {
		File file = getFileStreamPath("History.txt"); // Get default file path
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file)); // New file reader

			String read = ""; // Read line by line
			String count = ""; // Read push ups count
			String day = ""; // Read the day of the log
			String month = ""; // Read the month of the log
			String year = ""; // Read the year of the log

			// While there is text read line
			while ((read = bufferedReader.readLine()) != null) {

				count = (String) read.subSequence(0, read.indexOf(","));
				day = (String) read.subSequence(read.indexOf(",") + 1,
						read.indexOf("-"));
				month = (String) read.subSequence(read.indexOf("-") + 1,
						read.indexOf("/"));
				year = (String) read.substring(read.indexOf("/") + 1);

				if (Integer.valueOf(month) == MONTH
						&& Integer.valueOf(year) == YEAR) {
					pushUps[Integer.valueOf(day) - 1] += Integer.valueOf(count);
					calories[Integer.valueOf(day) - 1] += (Integer
							.valueOf(count)) * 0.825; // calories
				}
			}
			bufferedReader.close(); // Close buffer writer
		} catch (Exception e) {
		}
	}

}
