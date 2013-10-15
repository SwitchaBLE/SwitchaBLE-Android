package com.example.switchable_android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;

/*
 *  This class serves as a connector between the database information
 *  and the real Java objects it represents.
 */
public class AlarmDataSource {
	
	// database field variables
	private SQLiteDatabase database;
	private AlarmOpenHelper dbHelper;
	private String[] tableColumns = { AlarmOpenHelper.KEY_ID, AlarmOpenHelper.KEY_HOUR,
				AlarmOpenHelper.KEY_MINUTE, AlarmOpenHelper.KEY_STATUS};
	
	public AlarmDataSource(Context context) {
		dbHelper = new AlarmOpenHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	// adds alarm setup to database and returns BLE_Alarm instance
	public BLE_Alarm createBLE_Alarm(int hour, int minute, boolean isSet) {
		
		// establishes a set of key-value pairs
		ContentValues values = new ContentValues();
		values.put(AlarmOpenHelper.KEY_HOUR, hour);
		values.put(AlarmOpenHelper.KEY_MINUTE, minute);
		values.put(AlarmOpenHelper.KEY_STATUS, isSet);
		
		// method for inserting a row into the database
		long insertId = database.insert(AlarmOpenHelper.TABLE_NAME, null, values);
		
		// obtain cursor pointing to all rows of table
		Cursor cursor = database.query(AlarmOpenHelper.TABLE_NAME, tableColumns, 
				AlarmOpenHelper.KEY_ID + " = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();	// start at first row
		BLE_Alarm addedAlarm = rowToAlarm(cursor);
		cursor.close();
		
		return addedAlarm;
	}
	
	// deletes alarm setup from database
	public void deleteBLE_Alarm(BLE_Alarm alarm) {
		long id = alarm.getId();
		database.delete(AlarmOpenHelper.TABLE_NAME, AlarmOpenHelper.KEY_ID + " = " + id, null);
	}
	
	// edits the given alarm setup and updates database
	public void editBLE_Alarm(BLE_Alarm alarm, int hour, int minute, boolean isSet){
		
		// establishes a set of key-value pairs
		ContentValues values = new ContentValues();
		values.put(AlarmOpenHelper.KEY_HOUR, hour);
		values.put(AlarmOpenHelper.KEY_MINUTE, minute);
		values.put(AlarmOpenHelper.KEY_STATUS, isSet);
		
		long id = alarm.getId();
		database.update(AlarmOpenHelper.TABLE_NAME, values, AlarmOpenHelper.KEY_ID + " = " + id, null);
	}
	
	// retrieves all alarms and stores them in ArrayList
	public ArrayList<BLE_Alarm> getAllAlarms() {
		
		ArrayList<BLE_Alarm> alarmsList = new ArrayList <BLE_Alarm>();
		Cursor cursor = database.query(AlarmOpenHelper.TABLE_NAME, tableColumns, null,  
				null,  null,  null,  null);
		
		cursor.moveToFirst();
		// traverse the table's rows
		while (!cursor.isAfterLast()) {
			// convert each row to BLE_Alarm
			BLE_Alarm alarm = rowToAlarm(cursor);
			alarmsList.add(alarm);	// add to ArrayList
			cursor.moveToNext();
		}
		
		cursor.close();
		return alarmsList;
	}
	
	private BLE_Alarm rowToAlarm(Cursor cursor) {
		BLE_Alarm alarm = new BLE_Alarm();
		alarm.setId(cursor.getLong(0));
		alarm.setHour(cursor.getInt(1));
		alarm.setMinute(cursor.getInt(2));
		alarm.setStatus(cursor.getInt(3) == 1);
		
		return alarm;
	}
}
