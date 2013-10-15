package com.example.switchable_android;

import java.text.Format;
import java.text.SimpleDateFormat;

import android.text.format.Time;

public class BLE_Alarm {
	
	private long id;
	private int hour, minute;
	private boolean isSet;
	
	// default constructor for BLE_Alarm
	public BLE_Alarm() {}
	
	// constructor initializes private variables
	public BLE_Alarm(int hour, int minute, boolean isSet) {
		
		// initialize private variables
		this.hour = hour;
		this.minute = minute;
		this.isSet = isSet;
	}
	
	// setter method for alarm id
	public void setId(long id) {
		this.id = id;
	}
	
	// setter method for alarm hour
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	// setter method for alarm minute
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	// setter method for alarm status
	public void setStatus(boolean status) {
		isSet = status;
	}
	
	// getter method for all attributes
	public long getId() { return id; }
	public int getHour() { return hour; }
	public int getMinute() { return minute; }
	public boolean isSet() { return isSet; }
	
	// formats time in a consistent manner
	/*
	public String toString() {
		Time alarm_time = (new Time());
		alarm_time.set(0, minute, hour, 1, 1, 1);
		Format simple_time = new SimpleDateFormat("h:m a");
		return (simple_time.format(alarm_time)).toString();
	}
	*/
	public String toString() {
		if (hour == 0) {
			return "12:" + padding_str(minute) + " am";
		} else if (hour > 12) {
			return padding_str(hour - 12) + ":" + padding_str(minute) + " pm";
		} else if (hour < 10) {
			return "0" + hour + ":" + padding_str(minute) + " am";
		} else if (hour == 10 || hour == 11) {
			return hour + ":" + padding_str(minute) + " am";
		} else {
			return hour + ":" + padding_str(minute) + " pm";
		}
	}
	
	// adds a leading zero for formatting purposes
	private static String padding_str(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
}
