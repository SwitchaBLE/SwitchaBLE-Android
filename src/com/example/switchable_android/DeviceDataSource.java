package com.example.switchable_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;

/*
 *  This class serves as a connector between the database information
 *  and the real Java objects it represents.
 */
public class DeviceDataSource {
	
		// database field variables
		private SQLiteDatabase database;
		private DeviceOpenHelper dbHelper;
		private String[] tableColumns = { DeviceOpenHelper.KEY_ID, DeviceOpenHelper.KEY_DEVICE_NAME,
					DeviceOpenHelper.KEY_DEVICE_ADDR};
		
		public DeviceDataSource(Context context) {
			dbHelper = new DeviceOpenHelper(context);
		}
		
		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}
		
		public void close() {
			dbHelper.close();
		}
		
		// adds alarm setup to database and returns table row id
		public long addDevice(String name, String address) {
			
			// establishes a set of key-value pairs
			ContentValues values = new ContentValues();
			values.put(DeviceOpenHelper.KEY_DEVICE_NAME, name);
			values.put(DeviceOpenHelper.KEY_DEVICE_ADDR, address);
			
			// method for inserting a row into the database
			return database.insert(DeviceOpenHelper.TABLE_NAME, null, values);
		}
		
		// deletes alarm setup from database
		public void deleteDevice(String address) {
			Cursor cursor = database.query(DeviceOpenHelper.TABLE_NAME, tableColumns, 
					DeviceOpenHelper.KEY_DEVICE_ADDR + " = " + address, null, null, null, null);
			long id = cursor.getLong(0);
			database.delete(DeviceOpenHelper.TABLE_NAME, DeviceOpenHelper.KEY_ID + " = " + id, null);
		}
		
		public Cursor getDeviceCursor(){
			return database.query( DeviceOpenHelper.TABLE_NAME, 
					new String[]{DeviceOpenHelper.KEY_ID, DeviceOpenHelper.KEY_DEVICE_NAME}, null, null, null, null, null );
		}
//		// retrieves all devices and stores them in array
//		public String[] getDeviceNames() {
//			
//			Cursor cursor = database.query(DeviceOpenHelper.TABLE_NAME, tableColumns, null,  
//					null,  null,  null,  null);
//			String[] devices = new String[cursor.getCount()];
//			
//			cursor.moveToFirst();
//			// traverse the table's rows
//			while (!cursor.isAfterLast()) {
//				// convert each row to BLE_Alarm
//				BLE_Alarm alarm = rowToAlarm(cursor);
//				alarmsList.add(alarm);	// add to ArrayList
//				cursor.moveToNext();
//			}
//			
//			cursor.close();
//			return alarmsList;
//		}
//		
//		private BLE_Alarm rowToAlarm(Cursor cursor) {
//			BLE_Alarm alarm = new BLE_Alarm();
//			alarm.setId(cursor.getLong(0));
//			alarm.setHour(cursor.getInt(1));
//			alarm.setMinute(cursor.getInt(2));
//			alarm.setStatus(cursor.getInt(3) == 1);
//			
//			return alarm;
//		}
}