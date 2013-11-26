package com.example.switchable_android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;

public class AlarmArrayAdapter extends ArrayAdapter<BLE_Alarm> {

	private final Context context;
	
	// override of the parent class constructors
	public AlarmArrayAdapter(Context context, int resource) {
		super(context, resource);
		this.context = context;
	}
	
	public AlarmArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public AlarmArrayAdapter(Context context, int resource, BLE_Alarm[] objects) {
		super(context, resource, objects);
		this.context = context;
	}
	
	public AlarmArrayAdapter(Context context, int resource, int textViewResourceId, BLE_Alarm[] objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
	}
	
	public AlarmArrayAdapter(Context context, int resource, List<BLE_Alarm> objects) {
		super(context, resource, objects);
		this.context = context;
	}
	
	public AlarmArrayAdapter(Context context, int resource, int textViewResourceId, List<BLE_Alarm> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// obtains layout for context menu from XML resource
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.listview_row, parent, false);
		
		// obtains switch from context_menu.xml and sets text
		Switch alarm_switch = (Switch) row.findViewById(R.id.alarm_switch);
		alarm_switch.setText((CharSequence) getItem(position).toString());
		//alarm_switch.setChecked(true);
		
		return row;
	}
	
}
