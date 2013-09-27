package com.example.switchable_android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	// GUI private variables
	private ListView switches_listView;
	private AlarmArrayAdapter adapter;
	private Button alarm_button;
	
	// Database private variables
	private AlarmDataSource datasource;
	private ArrayList<BLE_Alarm> textViewAlarms;
	
	private int hour;
	private int minute;
	private int pressedId;
	
	static final int TIME_DIALOG_ID = 999;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize_db();
		initialize_gui();
		addButtonListener();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    //pressedId = switches_listView.getPositionForView(v);
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    pressedId = info.position;
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    
    	// obtains instance of BLE_Alarm selected in GUI
    	BLE_Alarm alarm = textViewAlarms.get(pressedId);
    	
	    switch (item.getItemId()) {
	    	
	        case R.id.context_menu_edit:
	            //editAlarm(info.id);
	            return true;
	            
	        case R.id.context_menu_delete:
	        	
	        	// delete BLE_Alarm from database and ArrayList
	            datasource.deleteBLE_Alarm(alarm);
	            textViewAlarms.remove(pressedId);
	            
	        	adapter.notifyDataSetChanged();		// updates GUI
	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private void addButtonListener() {
		alarm_button = (Button) findViewById(R.id.alarm_button);	// obtain from XML
		
		// add the "short" click listener
		alarm_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
				alarm_button.setFocusable(false);
			}
			
		});
		/*
		// add the "long" click listener
		alarm_button.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				promptDelete();
				return true;
			}
		}); */
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, timePickerListener, hour, minute, false);
		}
		return null;
	}
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			
			addAlarm(selectedHour, selectedMinute, true);

		}
	};
	
	private void addAlarm(int hour, int minute, boolean status) {
		
		// add alarm setup to database
		BLE_Alarm alarm = datasource.createBLE_Alarm(hour,  minute,  status);
		textViewAlarms.add(alarm);			// add alarm to ArrayList
		adapter.notifyDataSetChanged();		// notify adapter of changes

	}
	
	private void initialize_gui() {
		
		// initialize ListView paired with context menu
		switches_listView = (ListView) findViewById(R.id.switches_listView);
		registerForContextMenu(switches_listView);
		
		// initialize variables to populate ListView
		adapter = new AlarmArrayAdapter(switches_listView.getContext(), R.layout.listview_row, textViewAlarms);
		switches_listView.setAdapter(adapter);
				
		// set up listener for "Alarms" button
		addButtonListener();
	}
	
	private void initialize_db() {
		
		// create data source & obtain database
		datasource = new AlarmDataSource(this);
		datasource.open();
		
		// retrieve all alarms from database
		textViewAlarms = datasource.getAllAlarms();
	}

}
