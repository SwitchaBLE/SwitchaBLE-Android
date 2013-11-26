package com.example.switchable_android;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.provider.AlarmClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.bluetooth.bledemo.ScanningActivity;

public class MainActivity extends Activity {

	// GUI private variables
	private ListView switches_listView;
	private AlarmArrayAdapter adapter;
	private Button alarm_button, devices_button;
	
	// Database private variables
	private AlarmDataSource datasource;
	private ArrayList<BLE_Alarm> textViewAlarms;
	
	private int hour;
	private int minute;
	private boolean[] repeating;
	private int pressedId;
	private boolean alarmSelected;
	
	static final int TIME_DIALOG_ID = 999;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize_db();
		initialize_gui();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		
		if (!alarmSelected) {
            menu.findItem(R.id.action_new).setVisible(true);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.action_help).setVisible(true);
//            menu.findItem(R.id.scanning_indicator)
//                .setActionView(R.layout.progress_indicator);

        } else {
        	menu.findItem(R.id.action_new).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_help).setVisible(false);
//            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(MainActivity.this, AlarmSetupActivity.class);
		
		switch(item.getItemId()) {
		
			case R.id.action_new:
				MainActivity.this.startActivityForResult(intent, 1);
				break;
				
			case R.id.action_edit:
	        	intent.putExtra("hour",  textViewAlarms.get(pressedId).getHour());
	        	intent.putExtra("minute",  textViewAlarms.get(pressedId).getMinute());
	        	intent.putExtra("status", textViewAlarms.get(pressedId).isSet());
	        	//intent.putExtra("repeating", alarm.getRepeat());
	        	
	        	MainActivity.this.startActivityForResult(intent, 2);
	        	break;
	        	
			case R.id.action_delete:
	        	// delete BLE_Alarm from database and ArrayList
	            datasource.deleteBLE_Alarm(textViewAlarms.get(pressedId));
	            textViewAlarms.remove(pressedId);
	            
	        	adapter.notifyDataSetChanged();		// updates GUI
	        	Toast.makeText(this, R.string.alarm_deletion, Toast.LENGTH_SHORT).show();
				break;
				
			default:
				break;
		}
		
		alarmSelected = false;
		invalidateOptionsMenu();
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
	    
    	// obtains instance of BLE_Alarm selected in GUI
    	BLE_Alarm alarm = textViewAlarms.get(pressedId);
    	
    	alarmSelected = true;
    	invalidateOptionsMenu();
	    switch (item.getItemId()) {
	    	
	        case R.id.context_menu_edit:
	            
//	        	// create intent to edit an existing alarm
//	        	Intent intent = new Intent(MainActivity.this, AlarmSetupActivity.class);
//	        	intent.putExtra("hour",  alarm.getHour());
//	        	intent.putExtra("minute",  alarm.getMinute());
//	        	intent.putExtra("status", alarm.isSet());
//	        	//intent.putExtra("repeating", alarm.getRepeat());
//	        	
//	        	MainActivity.this.startActivityForResult(intent, 2);
//	            return true;
	            
	        case R.id.context_menu_delete:
	        	
//	        	// delete BLE_Alarm from database and ArrayList
//	            datasource.deleteBLE_Alarm(alarm);
//	            textViewAlarms.remove(pressedId);
//	            
//	        	adapter.notifyDataSetChanged();		// updates GUI
//	        	Toast.makeText(this, R.string.alarm_deletion, Toast.LENGTH_LONG).show();
//	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private void addButtonListeners() {
		alarm_button = (Button) findViewById(R.id.alarm_button);	// obtain from XML
		devices_button = (Button) findViewById(R.id.devices_button);
		
		// add the "short" click listener
		alarm_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showDialog(TIME_DIALOG_ID);
				//alarm_button.setFocusable(false);
				
				Intent intent = new Intent(MainActivity.this, AlarmSetupActivity.class);
				MainActivity.this.startActivityForResult(intent, 1);
			}
			
		});
		
		devices_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				Intent intent = new Intent(MainActivity.this, ScanningActivity.class);
				startActivity(intent);
			}
			
		});
		
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK) {
			
			//retrieving setup from intent
			hour = data.getIntExtra("hour", 0);
			minute = data.getIntExtra("minute", 0);
			repeating = data.getBooleanArrayExtra("repeating");
			
			if(requestCode == 1) {		// new alarm created
				
				addAlarm(hour, minute, true);
				scheduleAlarm(hour, minute);
				
			} else if(requestCode == 2) {	// alarm edited
				
				editAlarm(hour, minute, true);
				Toast mToast = Toast.makeText(this, "Alarm edited.", Toast.LENGTH_LONG);
				mToast.show();
			}
		}
	}
	
	private void addAlarm(int hour, int minute, boolean status) {
		
		// add alarm setup to database
		BLE_Alarm alarm = datasource.createBLE_Alarm(hour,  minute,  status);
		textViewAlarms.add(alarm);			// add alarm to ArrayList
		adapter.notifyDataSetChanged();		// notify adapter of changes
	}
	
	private void editAlarm(int hour, int minute, boolean status) {
		
		BLE_Alarm alarm = textViewAlarms.get(pressedId);
		datasource.editBLE_Alarm(alarm, hour, minute, status);
		textViewAlarms.get(pressedId).setHour(hour);
		textViewAlarms.get(pressedId).setMinute(minute);
		textViewAlarms.get(pressedId).setStatus(status);
		adapter.notifyDataSetChanged();
	}
	
	// attempt to schedule using AlarmManager
	private void scheduleAlarm(int hour, int minute) {
		Intent intent = new Intent(this, OneShotAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		
		Toast mToast = Toast.makeText(this, "Alarm scheduled", Toast.LENGTH_LONG);
		mToast.show();
	}
	
	private void initialize_gui() {
		
		alarmSelected = false;
		
		// initialize ListView paired with context menu
		switches_listView = (ListView) findViewById(R.id.switches_listView);
		//registerForContextMenu(switches_listView);
		
		switches_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    int index, long arg3) {
            	
                alarmSelected = true;
                invalidateOptionsMenu();
                pressedId = index;
                return true;
            }
});
		
		// initialize variables to populate ListView
		adapter = new AlarmArrayAdapter(switches_listView.getContext(), R.layout.listview_row, textViewAlarms);
		switches_listView.setAdapter(adapter);
		
		// set up listener for "Alarms" button
		addButtonListeners();
	}
	
	private void initialize_db() {
		
		// initialize private variables
		hour = 0;
		minute = 0;
		repeating = new boolean[7];
		
		// create data source & obtain database
		datasource = new AlarmDataSource(this);
		datasource.open();
		
		// retrieve all alarms from database
		textViewAlarms = datasource.getAllAlarms();
	}

	public void onDestroy() {
		super.onDestroy();
		datasource.close();
	}
}
