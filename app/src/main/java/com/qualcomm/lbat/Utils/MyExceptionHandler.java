package com.qualcomm.lbat.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.qualcomm.lbat.MainActivity;
import com.qualcomm.lbat.R;
import com.qualcomm.lbat.SmarTestApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

/**
 * Created by shrishn on 3/2/2018.
 */

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity activity;
    private Context context;
    private static final String TAG = "STExceptionHandler";

    public MyExceptionHandler(Activity a) {
        activity = a;
//        context = a.getApplicationContext();
//        Log.i(TAG,"SmarTest Exception handler instantiated");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.e(TAG,"SMARTEST CRASHED : " + ex.getMessage());

        try {
            String root = Constants.SDCARD + "/";
            String serial = Build.SERIAL;
            Calendar c = Calendar.getInstance();
            String today = c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DATE);
            String filename = root + today + "_" + serial + "_" + SmarTestApplication.getInstance().getBaseContext().getString(R.string.app_version_num) + "_Crash.csv";
            Log.i(TAG, "Filename : " + filename);
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter out;
            String data = "";
            if (ex.getMessage() != null) {
                data += ex.getMessage().toString() + ",";
            }
            if (ex != null) {
                StackTraceElement stackTraceElement[] = ex.getStackTrace();
                for (StackTraceElement s : stackTraceElement) {
                    data += s.toString() + " | ";
                }
                data += "," + "\r";
                ;
            }
            out = new BufferedWriter(new FileWriter(filename, true));
            out.write(data);
            out.newLine();
            out.close();
            Log.i(TAG, "LAUNCHING SMARTEST AGAIN");
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("crash", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(SmarTestApplication.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager mgr = (AlarmManager) SmarTestApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            activity.finish();
            if(activity.isDestroyed()) {
                Log.i(TAG,"ACTIVITY IS DESTROYED. FINISHING NOW!");
            }  else{
                Log.i(TAG,"ACTIVITY IS NOT DESTROYED");
            }
//            System.exit(2);
//            android.os.Process.killProcess(SmarTestApplication.getInstance().getUserId());
            System.exit(2);

//            new Thread() {
//                @Override
//                public void run() {
//                    Looper.prepare();
//                    Log.i(TAG,"LAUNCHING SMARTEST AGAIN");
////                    Toast.makeText( SmarTestApplication.getInstance().getBaseContext(), "Application crashed", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    intent.putExtra("crash", true);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    PendingIntent pendingIntent = PendingIntent.getActivity(SmarTestApplication.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//                    AlarmManager mgr = (AlarmManager) SmarTestApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
////                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 20000, pendingIntent);
//                    mgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pendingIntent);
//
//                    Looper.loop();
//
//                    activity.finish();
//                    System.exit(0);
//                }
//            }.start();
//
//
//            Thread.sleep(15000);

        }catch(Exception e) {
            Log.e(TAG,e.toString());
        }
    }
}