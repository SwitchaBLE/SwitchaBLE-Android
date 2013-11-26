package com.example.switchable_android;

import org.bluetooth.bledemo.HRDemoActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 * <p>
 * When the alarm goes off, we show a <i>Toast</i>, a quick message.
 */
public class OneShotAlarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent){
    	
    	Intent i = new Intent(context, HRDemoActivity.class);
    	//Intent i = new Intent(context, MainActivity.class);
    	i.putExtra(HRDemoActivity.EXTRAS_DEVICE_NAME, "SwitchaBLE");
        i.putExtra(HRDemoActivity.EXTRAS_DEVICE_ADDRESS, "CA:25:91:BE:DA:C4");
        i.putExtra(HRDemoActivity.EXTRAS_DEVICE_RSSI, -75);
    	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(i);
    	
    }
}
