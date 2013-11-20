package com.example.switchable_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TimePicker;

public class AlarmSetupActivity extends Activity {
	
	private int hour;
	private int minute;
	private boolean[] repeating;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_setup);
		
		initialize_gui();
		initialize_data();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// method sets up data (if needed) before returning to main view
	public void onBottomButtonClick(View v) {
		
		Intent intent = new Intent();
		
		// pass alarm setup if "ok" was selected
		if(v.getId() == R.id.alarm_setup_ok) {
			
			intent.putExtra("hour", hour);
			intent.putExtra("minute", minute);
			intent.putExtra("repeating", repeating);
			setResult(RESULT_OK, intent);
			finish();
			
		}else if(v.getId() == R.id.alarm_setup_cancel) {
			
			// return to main activity without passing data
			setResult(RESULT_CANCELED, intent);
			finish();
		}
	}
	
	private void initialize_gui() {
		
		// setting the listener for the TimePicker view
		TimePicker timeView = (TimePicker) findViewById(R.id.alarm_setup_timePicker);
		timeView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
		
			public void onTimeChanged(TimePicker view, int selectedHour, int selectedMinute) {
				hour = selectedHour;
				minute = selectedMinute;
			}
		});
		
//		Spinner spinnerDevices = (Spinner) findViewById(R.id.spinner_device);
//		spinnerDevices.setAdapter(adapterDevices);
	}
	
	private void initialize_data() {
		
		Bundle data = getIntent().getExtras();
		
		if(data != null) {
			hour = data.getInt("hour");
			minute = data.getInt("minute");
			//repeating = data.getBooleanArray("repeating");
		}else{
			hour = 0;
			minute = 0;
			repeating = new boolean[7];
		}
	}
}
