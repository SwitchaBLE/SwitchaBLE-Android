package com.example.switchable_android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DeviceOpenHelper extends SQLiteOpenHelper {
		// database info variables
		private static final String DATABASE_NAME = "switchable.db";
		private static final int DATABASE_VERSION = 2;
		
		// table info variables
		public static final String TABLE_NAME = "devices";
		public static final String KEY_ID = "_id";
		public static final String KEY_DEVICE_NAME = "name";
		public static final String KEY_DEVICE_ADDR = "address";
		private static final String TABLE_CREATE =
				"CREATE TABLE " + TABLE_NAME + " (" +	KEY_ID + 
				" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DEVICE_NAME + " TEXT, " +
				KEY_DEVICE_ADDR + " TEXT);";
		
		public DeviceOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(AlarmOpenHelper.class.getName(), "Upgrading database from version " +
					oldVersion + " to " + newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
}