package com.qualcomm.lbat.Services.Common;

/**
 * Created by shrishn on 3/21/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.qualcomm.lbat.MainActivity;
import com.qualcomm.lbat.SmarTestApplication;
import com.qualcomm.lbat.Utils.Constants;
import com.qualcomm.lbat.Utils.Util;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    private static final String TAG = "Boot_Complete";
    private Util util;
    private String fileName;
    private SmarTestApplication smartestApp;

    public void outputMessage(String message)
    {
        try
        {
            Log.i(TAG,message);
            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }catch( Exception e)	{ }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        util = new Util();
        util.setContext(context.getApplicationContext());
//        shutdlr = new ShutdownLogResults();
//        if (!new File(fileName).exists()) {
//            fileName = util.createLogFile(Constants.SHUTDOWN);
//            util.writeHeading(fileName, Constants.SHUTDOWN_LOG_RESULT);
//        }

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            int shutDownCode = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(
                    Constants.SHARED_SHUT_DOWN_STATUS, 0);
            int shutDownCount = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(
                    Constants.SHARED_SHUT_DOWN_COUNT, 0);

            if (shutDownCode == 1)
            {
                // Reset the last value
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
                editor.putInt(Constants.SHARED_SHUT_DOWN_STATUS, 0);
                editor.putString(Constants.SHUTDOWN, Constants.SHUTDOWN_STATUS_NORMAL);
                if (shutDownCount == 0) {
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, 1);
                } else {

                    shutDownCount += 1;
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, shutDownCount);
                }
                editor.commit();
                //shutdlr.LogResult = "NORMAL_SHUTDOWN";
                outputMessage("ShutDown Triggered");

            }
            else if (shutDownCode == 0)
            {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
                editor.putInt(Constants.SHARED_SHUT_DOWN_STATUS, 0);
                editor.putString(Constants.SHUTDOWN, Constants.SHUTDOWN_STATUS_ABBRUPT);
                if (shutDownCount == 0) {
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, 1);
                } else {

                    shutDownCount += 1;
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, shutDownCount);
                }
                editor.commit();
                //shutdlr.LogResult = "ABRUPT_SHUTDOWN";
                outputMessage("ShutDown Not Triggered");

            }
            else if (shutDownCode == 2)
            {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
                editor.putInt(Constants.SHARED_SHUT_DOWN_STATUS, 0);
                editor.putString(Constants.SHUTDOWN, Constants.SHUTDOWN_STATUS_SMT);
                if (shutDownCount == 0) {
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, 1);
                } else {
                    shutDownCount += 1;
                    editor.putInt(Constants.SHARED_SHUT_DOWN_COUNT, shutDownCount);
                }
                editor.commit();
                //shutdlr.LogResult = "ABRUPT_SHUTDOWN";
                outputMessage("ShutDown Triggered SMT");
            }

            Intent pushIntent = new Intent(context, MainActivity.class);
            pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pushIntent);

//            shutdlr.LogIteration = shutDownCount+1;
//            shutdlr.LogStartTime = util.getTime(":");
//            util.writeDataToLogFile(fileName,shutdlr.getShutdownLogResults());
//            shutdlr.reset();

        }
        else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN))
        {
            outputMessage("Action Shutdown");
            // Saving value in Preferences for ShutDown broadcast
            int shutDownCode = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(
                    Constants.SHARED_SHUT_DOWN_STATUS, 0);
            if(shutDownCode == 0) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
                editor.putInt(Constants.SHARED_SHUT_DOWN_STATUS, 1);
                editor.putString(Constants.SHUTDOWN_START_TIME, util.getTime(":"));
                editor.commit();
            }
        }
    }
}