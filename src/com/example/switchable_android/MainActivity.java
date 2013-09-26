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

	private Button alarm_button;
	private ArrayList<BLE_Alarm> textViewAlarms;
	private AlarmArrayAdapter adapter;
	private ListView switches_listView;
	
	private int hour;
	private int minute;
	
	static final int TIME_DIALOG_ID = 999;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
	    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    
	    
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.context_menu_edit:
	            //editAlarm(info.id);
	        	
	            return true;
	        case R.id.context_menu_delete:
	            //deleteNote(info.id);
	        	
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
			
			//addAlarmButton();
			addAlarmSwitch();
			//this is where the alarm will be created for the system
		}
	};

	private void addAlarmButton() {
		// create new Button to house time setup
		Button alarmAdded = new Button(getApplicationContext());
		alarmAdded.setText(new StringBuilder().append(padding_str(hour)).append(":").append(padding_str(minute)));
		
		LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
		main_layout.addView(alarmAdded);	// add new Button to LinearLayout
		
		//textViewAlarms.add(alarmAdded);		// store alarm setup
		//textViewAlarms.add(padding_str(hour)+":"+padding_str(minute));
	}
	
	public void addAlarmSwitch() {
		// create switch and modify looks
		/*
		Switch toggle = new Switch(getApplicationContext());
		toggle.setText(new StringBuilder().append(padding_str(hour)).append(":").append(padding_str(minute)));
		toggle.setTextSize(40);		// this should not be a constant
		toggle.setPadding(40,0,40,0);
		registerForContextMenu(toggle);
		
		//set switch on/off functionality
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        } else {
		            // The toggle is disabled
		        }
		    }
		});
		*/
		BLE_Alarm alarm = new BLE_Alarm(hour, minute, true);
		textViewAlarms.add(alarm);
		adapter.notifyDataSetChanged();
	}
	
	// Create Dialog to prompt for alarm deletion
	private void promptDelete() {
		AlertDialog.Builder alertDelete = new AlertDialog.Builder(this);
		 
		alertDelete.setTitle("SwitchaBLE");
		alertDelete.setMessage("Delete this alarm?");
		 
		// configuring "ok" button and response
		alertDelete.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                // Write your code here to execute after dialog
		            	
		            	// insert code here to delete alarm
		            }
		        });
		
		// configuring "cancel" button and response
		alertDelete.setNegativeButton("Cancel",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                // Write your code here to execute after dialog

		            	// do nothing here
		            }
		        });
		 
		alertDelete.show();
	}
	
	private static String padding_str(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	private void initialize_gui() {
		
		// initialize ListView paired with context menu
		switches_listView = (ListView) findViewById(R.id.switches_listView);
		registerForContextMenu(switches_listView);
		
		// initialize variables to populate ListView
		textViewAlarms = new ArrayList<BLE_Alarm>();
		adapter = new AlarmArrayAdapter(switches_listView.getContext(), R.layout.listview_row, textViewAlarms);
		switches_listView.setAdapter(adapter);
				
		// set up listener for "Alarms" button
		addButtonListener();
	}
}
