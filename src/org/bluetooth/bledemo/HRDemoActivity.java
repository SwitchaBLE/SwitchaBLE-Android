package org.bluetooth.bledemo;

import com.example.switchable_android.AlarmSetupActivity;
import com.example.switchable_android.MainActivity;
import com.example.switchable_android.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/* this activity's purpose is to show how to use particular type of devices in easy and fast way */
public class HRDemoActivity extends Activity {
	public static final String EXTRAS_DEVICE_NAME    = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI    = "BLE_DEVICE_RSSI";
	
	private Handler mHandler = null;
	private BluetoothManager mBTManager = null;
	private BluetoothAdapter mBTAdapter = null;
	private BluetoothDevice  mBTDevice = null;
	private BluetoothGatt    mBTGatt = null;
	private BluetoothGattService        mBTService = null;
	private BluetoothGattCharacteristic mBTValueCharacteristic = null;
	private byte[] byteValue;
	// UUDI of Switchable service/characteristics:
	final static private UUID mSwitchableServiceUuid = BleDefinedUUIDs.Service.SWITCHABLE;
	final static private UUID mLightStateCharacteristicUuid = BleDefinedUUIDs.Characteristic.LIGHT_STATE;
	
	private EditText mConsole = null;
	private TextView mTextView  = null;
	private Button bOff = null;
	private Button bOn = null;
	private Button bToggle = null;
	private Button bPulse = null;
	private Button bStrobe = null;
	
	private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BleWrapper mBleWrapper;
    
    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceRssiView;
    private ListView mListView;
    private View     mListViewHeader;
    private TextView mHeaderTitle;
    private TextView mHeaderBackButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_peripheral);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_hrdemo);
		
		connectViewsVariables();
		
		final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0) + " db";
        mDeviceNameView.setText(mDeviceName);
        mDeviceAddressView.setText(mDeviceAddress);
        mDeviceRssiView.setText(mDeviceRSSI);
//        getActionBar().setTitle(mDeviceName);
		mConsole = (EditText) findViewById(R.id.hr_console_item);
		log("Creating activity");
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Control Panel");
		mConsole = (EditText) findViewById(R.id.hr_console_item);
		mTextView = (TextView) findViewById(R.id.hr_text_view);
		
		// retrieving buttons and setting listeners
		bOff = (Button) findViewById(R.id.turnOff_char);
		bOff.setOnClickListener(myClickListener);
		bOn = (Button) findViewById(R.id.turnOn_char);
		bOn.setOnClickListener(myClickListener);
		bToggle = (Button) findViewById(R.id.toggle_char);
		bToggle.setOnClickListener(myClickListener);
		bPulse = (Button) findViewById(R.id.pulse_char);
		bPulse.setOnClickListener(myClickListener);
		bStrobe = (Button) findViewById(R.id.strobe_char);
		bStrobe.setOnClickListener(myClickListener);
		
		mHandler = new Handler();
		log("Activity created");
	}
	
	View.OnClickListener myClickListener= new View.OnClickListener() {
	    public void onClick(View v) {	
	    	
	    	byte[] value = new byte[1];	    	
	    	// determine device command to be sent
	    	switch (v.getId()) {

	    		case R.id.turnOff_char:		// turn off light
	    			value[0] = 0;	    			
	    			break;
	    		case R.id.turnOn_char:		// turn on light
	    			value[0] = 1 << 0;	    			
	    			break;
	    		case R.id.toggle_char:		// toggle light
	    			value[0] = 1 << 1;	    			
	    			break;
	    		case R.id.pulse_char:		// pulse light
	    			value[0] = 1 << 2;	    			
	    			break;
	    		case R.id.strobe_char:		// strobe light
	    			value[0] = 1 << 3;
	    			break;
	    		default:					// incorrect command
	    			value[0] = 1 << 6;
	    			break;
	    	}
	    	
	    	// writing command to light state characteristic
	    	mBTValueCharacteristic.setValue(value);
	    	log("Wrote this value to the characteristic:" + value[0]);
			mBTGatt.writeCharacteristic(mBTValueCharacteristic);
	    }
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		log("Resuming activity");
		
		// first check if BT/BLE is available and enabled
		if(initBt() == false) return;
		if(isBleAvailable() == false) return;
		if(isBtEnabled() == false) return;
		
		// then start discovering devices around
		//startSearchingForHr();
		startSearchingForS();
		//mBTAdapter.getRemoteDevice(mDeviceAddress);
		//connectToDevice();
		//discoverServices();
//		if(mBTAdapter == null)
//			log("It was null");
		
		log("Activity resumed");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		disableNotificationForS();
		disconnectFromDevice();
		closeGatt();
	};

	private boolean initBt() {
		mBTManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		if(mBTManager != null) mBTAdapter = mBTManager.getAdapter();
		
		return (mBTManager != null) && (mBTAdapter != null);
	}
	
	private boolean isBleAvailable() {
		log("Checking if BLE hardware is available");
		
		boolean hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if(hasBle && mBTManager != null && mBTAdapter != null) {
			log("BLE hardware available");
		}
		else {
			log("BLE hardware is missing!");
			return false;
		}
		return true;
	}
	
	private boolean isBtEnabled() {
		log("Checking if BT is enabled");
		if(mBTAdapter.isEnabled()) {
			log("BT is enabled");
		}
		else {
			log("BT is disabled. Use Setting to enable it and then come back to this app");
			return false;
		}
		return true;
	}

	private void startSearchingForS() {
		// removed scanning for devices given specific UUIDs because this
		// feature is currently buggy as of API Level 18
		mBTAdapter.startLeScan(mDeviceFoundCallback);
		
		// results will be returned by callback
		log("Searching for BLE devices.");

		// please, remember to add timeout for that scan
		Runnable timeout = new Runnable() {
            @Override
            public void run() {
				if(mBTAdapter.isDiscovering() == false) return;
				stopSearchingForS();	
            }
        };
        mHandler.postDelayed(timeout, 1000); //10 seconds		
	}
	
	private void stopSearchingForS() {
		mBTAdapter.stopLeScan(mDeviceFoundCallback);
		log("Stopped searching for BLE devices.");
	}
	
	private void connectToDevice() {
		log("Connecting to the device NAME: " + mBTDevice.getName() + " HWADDR: " + mBTDevice.getAddress());
		mBTGatt = mBTDevice.connectGatt(this, true, mGattCallback);
	}
	
	private void disconnectFromDevice() {
		log("Disconnecting from device.");
		if(mBTGatt != null) mBTGatt.disconnect();
	}
	
	private void closeGatt() {
		if(mBTGatt != null) mBTGatt.close();
		mBTGatt = null;
	}
	
	private void discoverServices() {
		log("Discovering device's services.");
		mBTGatt.discoverServices();
	}
	
	private void getSwitchableService() {
		log("Getting Light State Service");
		mBTService = mBTGatt.getService(mSwitchableServiceUuid);
		
		if(mBTService == null) {
			log("Could not get Heart Rate Service");
		}
		else {
			log("Switchable Service successfully retrieved");
			getLightStateCharacteristic();
		}
	}
	
	private void getLightStateCharacteristic() {
		log("Getting Light State characteristic");
		mBTValueCharacteristic = mBTService.getCharacteristic(mLightStateCharacteristicUuid);
		
		if(mBTValueCharacteristic == null) {
			log("Could not find Light State Characteristic");
		}
		else {
			log("Light State characteristic retrieved properly");
			enableNotificationForLightState();
		}
	}

	private void enableNotificationForLightState() {
		log("Enabling notification for Light State");
        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, true);
        if(!success) {
        	log("Enabling notification failed!");
        	return;
        }

//        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
//        if(descriptor != null) {
//	        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//	        mBTGatt.writeDescriptor(descriptor);
//	        log("Notification enabled");
//        }		
//        else {
//        	log("Could not get descriptor for characteristic! Notification are not enabled.");
//        }
	}
	
	private void disableNotificationForS() {
		log("Disabling notification for Light State");
        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, false);
        if(!success) {
        	log("Disabling notification failed!");
        	return;
        }

        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
        if(descriptor != null) {
	        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	        mBTGatt.writeDescriptor(descriptor);
	        log("Notification disabled");
        }		
        else {
        	log("Could not get descriptor for characteristic! Notification could be still enabled.");
        }
	}	
	
    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	// here we found some device BLE capability
        	HRDemoActivity.this.mBTDevice = device;
        	log("BLE device discovered.. HW Address: "  + device.getAddress());
        	stopSearchingForS();
        	
        	connectToDevice();
        }
    };	
	
    /* callbacks called for any action on HR Device */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	log("Device connected");
            	discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	log("Device disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	if(status == BluetoothGatt.GATT_SUCCESS) {
        		log("Services discovered");
        		getSwitchableService();
        	}
        	else {
        		log("Unable to discover services");
        	}
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
//        	byte[] raw = mBTValueCharacteristic.getValue();
//        	int value = 0;
//        	int index = ((raw[0] & 0x01) == 1) ? 2 : 1;
//        	int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
//     
//        	//int value = mBTValueCharacteristic.getIntValue(format, index);
//        	int newValue = 0;
//			if (index == 2) {
//				newValue = 0;
//			}else{
//				newValue = 1;
//			}
//			
//			byteValue = (Integer.toString(newValue)).getBytes();
//			log("Wrote this value to the characteristic:" + newValue + " and ");
//			mBTValueCharacteristic.setValue(byteValue);
//			mBTGatt.writeCharacteristic(mBTValueCharacteristic);
        }       
        
        /* the rest of callbacks are not interested for us */
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	byte[] raw = mBTValueCharacteristic.getValue();
        	
        	int value = 0;
        	int index = ((raw[0] & 0x01) == 1) ? 2 : 1;
        	int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
     
        	//int value = mBTValueCharacteristic.getIntValue(format, index);
        	int newValue = 0;
			if (index == 2) {
				newValue = 0;
			}else{
				newValue = 1;
			}
			
			byteValue = toBytes(newValue);
			log("Wrote this value to the characteristic:" + newValue + " and " + byteValue);
			mBTValueCharacteristic.setValue(byteValue);
			mBTGatt.writeCharacteristic(mBTValueCharacteristic);
        }


        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {};
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {};
    };
     
	
	// put new logs into the UI console
	private void log(final String txt) {
		if(mConsole == null) return;
		
		final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConsole.setText(timestamp + " : " + txt + "\n" + mConsole.getText());
			}		
		});
	}
	
	byte[] toBytes(int i)
	{
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  return result;
	}
	
	@Override
	public void onBackPressed() {
		disableNotificationForS();
		disconnectFromDevice();
		closeGatt();
		super.onBackPressed();
	}
	private void connectViewsVariables() {
    	mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
		mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
		mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
		//mListView = (ListView) findViewById(R.id.listView);
		//mHeaderTitle = (TextView) mListViewHeader.findViewById(R.id.peripheral_service_list_title);
		//mHeaderBackButton = (TextView) mListViewHeader.findViewById(R.id.peripheral_list_service_back);
    }
}
