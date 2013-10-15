package com.example.switchable_android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;

public class bleDeviceActivity extends Activity{

	private BluetoothAdapter mBluetoothAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble_devices);
		
		setupBLE();
	}
	
	private void setupBLE() {
		
		// initializes Bluetooth adapter
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		// ensures Bluetooth is available on the device and it is enabled
		// if not, displays dialog requesting permission to enable it
	}
}
