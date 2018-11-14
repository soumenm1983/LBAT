package com.qualcomm.lbat.Services.Common;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.qualcomm.lbat.Classes.GPS;
import com.qualcomm.lbat.Utils.Constants;
import com.qualcomm.lbat.Utils.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
 *  GPS Service
 *
 *  ask the system every few seconds for new GPS cordniats
 */
public class GPS_Service extends Service implements LocationListener {
	private LocationManager locManager;
	private String GPSLatitude;
	private String GPSLongitude;
	private Util util;
	private long timeSpan;
	private String gpsTimeSpan;
	private int gpsOnTime;
	private TelephonyManager telephonyManager;
	private GPS gps;
	private int minTimeSpan;
	private int maxTimeSpan;
	private String fileName;
	private String beforeEnable;
	private static int gpsCounter;
	public static String TAG = "GPS";
	public static String TODO = "PLZ ENABLE LOCATION SERVICE IN SETTINGS FOR LBAT APP.";
	private boolean isSeriesExe;
	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double altitude;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
			util = new Util();
			util.setContext(this);
			telephonyManager = util.getTelephonyManager();
			locManager = util.getLocationManager();
//            locManager.requestLocationUpdates("gps", 5000, 10, this);
			gpsCounter = 0;
			//util.serviceDoneBroadcast();
			outputMessage("On create GPS called ");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			outputMessage("On Start CMD GPS called ");
			gps = (GPS) intent.getSerializableExtra("obj");
			isSeriesExe = (boolean) intent.getSerializableExtra("SeriesType");
			//timeSpan = gps.toggleDuration * 60000;
			util = new Util();
			util.setContext(this);
			outputMessage("On Start CMD GPS called *** " + gps.gpsOnTimer);
			gpsOnTime = gps.gpsOnTimer;
			gpsTimeSpan = gps.gpsTimeRange;
			String[] temp = gpsTimeSpan.split(":");
			minTimeSpan = Integer.parseInt(temp[0]);
			maxTimeSpan = Integer.parseInt(temp[1]);
			Random rand = new Random();
			timeSpan = rand.nextInt(maxTimeSpan - minTimeSpan + 1) + minTimeSpan;
			timeSpan = timeSpan * 60000;
			Log.i(TAG, "GPS start command");
			mainloop();

			isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//

			if (isNetworkEnabled) {
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);
				if (locManager != null) {
					location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						altitude = location.getAltitude();
					}
				}
			}

			if (isGPSEnabled) {
				if (location == null) {
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);

					Log.d("GPS Enabled", "GPS Enabled");
					if (locManager != null) {
						location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							altitude = location.getAltitude();
						}
					}
				}
			}

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return START_REDELIVER_INTENT;
	}

	public void mainloop() {
		outputMessage("Mainloop GPS called ");
		util.unlock_screen(getApplicationContext());
		int itCount = PreferenceManager.getDefaultSharedPreferences(util.getContext()).getInt(
				"GPS_Counter", 0);
		gpsCounter = itCount;
		fileName = logFile();
		gpsCounter++;


		Log.i("GPS Service Started", "GPS main loop started");
		if (!isSeriesExe) {
			if (gpsOnTime > 0) {
				if (gpsCounter % 2 == 0) {
					Log.d(TAG, "mainloop: Turn off gps called");
					turnOffGps();
				} else {
					Log.d(TAG, "mainloop: Turn on gps called");
					turnOnGps();
				}

				Timer GPS = new Timer();
				try {
					GPS.schedule(new TimerTask() {
						public void run() {
							if (fileName != null) {
								//writeDataToLogFile(fileName);
								SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(util.getContext()).edit();
								editor.putInt("GPS_Counter", gpsCounter);
								editor.commit();
							}
							mainloop();
						}
					}, timeSpan);
				} catch (Exception e) {
				}
			} else {
				turnOnGps();
			}
		} else {
			turnOnGps();
			final Timer waitTimer = new Timer();
			try {
				waitTimer.schedule(new TimerTask() {
					public void run() {
						turnOffGps();
					}
				}, (gpsOnTime * 1000));
			} catch (Exception e) {
			}
		}
	}

	public boolean getGPSLoc() {
		try {
			Location loc = locManager.getLastKnownLocation("gps");
			if(loc != null) {
				GPSLatitude = "" + loc.getLatitude();
				GPSLongitude = "" + loc.getLongitude();		
				Log.i(TAG,GPSLatitude+"   "+GPSLongitude);
			}
			else {
				GPSLatitude = "No GPS";
				GPSLongitude = "No GPS";
				Log.i(TAG,GPSLatitude+"   "+GPSLongitude);
				return false;
			}
    	}
    	catch( Exception e)
    	{
    		GPSLatitude = "No GPS";
			GPSLongitude = "No GPS";
			return false;
    	}
		return true;
    }

    public void turnOnGps() {
		Log.d(TAG, "turnOnGps: ");

		String cmd = "settings put secure location_providers_allowed +gps";
		util.runCmd(cmd);
		timeSpan = (gpsOnTime*1000);
		//getGPS();
	}

	public void turnOffGps(){
		Log.d(TAG, "turnOffGps: ");

		String cmd = "settings put secure location_providers_allowed -gps";
		util.runCmd(cmd);
		Random rand = new Random();
		timeSpan = rand.nextInt(maxTimeSpan - minTimeSpan+1) + minTimeSpan;
		timeSpan = timeSpan * 60000;
	}

	public void outputMessage(final String message) {
		try {
			Log.i(TAG, message);
			Handler h = new Handler(getApplicationContext().getMainLooper());

			h.post(new Runnable()
			{
				public void run()
				{
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				}
			});
			//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		}catch( Exception e)	{ }
	}

	public String logFile()
	{
		String filename = "";
		try
		{
			String root = Constants.SDCARD + "/logs/";
			String serial = Build.SERIAL;
			String today = util.getDate();
			filename = root + today + "_" +serial +"_GpsService.csv";

			File dir = new File(root);
			if(!dir.exists())
			{
				if(!dir.mkdirs())
				{
					Log.i("dir prob not exist", root);
					return null;
				}
			}

			File out = new File(filename);
			if(!out.exists())
			{
				out.createNewFile();
				//writeHeading(filename);
			}
		}
		catch(Exception e)
		{
			Log.i("exp","file");
			return null;
		}
		return filename;
	}
	
	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		Log.i("GPS Service", "onDestroy()");
		super.onDestroy();
	}	

	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}	
	
}

