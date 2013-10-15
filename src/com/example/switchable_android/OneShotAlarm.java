package com.example.switchable_android;

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
    	    	
    	Intent i = new Intent(context, MainActivity.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(i);
    	
    }
}
