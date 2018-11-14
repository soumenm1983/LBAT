package com.qualcomm.lbat.Services.Common;
/*
 *  gets information, like  time and so on from the operating system
 */

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Date;

public class BroadcastService extends Service
{	
	public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			intent = new Intent(BROADCAST_ACTION);
		}catch(Exception e){

		}
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
    	try {
    		handler.removeCallbacks(sendUpdatesToUI);
    		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    	}catch(Exception e){

        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run()
    	{
    		try {
    			DisplayLoggingInfo();    		
    			handler.postDelayed(this, 1000); // 0.1 seconds // 10000 = 10 secounds // update time
    		}catch(Exception e){

            }
    	}
    };    
    
    @SuppressWarnings("deprecation")
	private void DisplayLoggingInfo() {
        try {
    		intent.putExtra("time", new Date().toLocaleString());
    		sendBroadcast(intent);
    	}catch(Exception e){

        }
    }
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy() {
		try {
			handler.removeCallbacks(sendUpdatesToUI);	
		}catch(Exception e){

        }
		
		super.onDestroy();
	}		
}
