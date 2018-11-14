package com.qualcomm.lbat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telecom.PhoneAccountHandle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.qualcomm.lbat.Controller.Scenarios;
import com.qualcomm.lbat.Controller.ScenariosManager;
import com.qualcomm.lbat.Services.Common.BroadcastService;
import com.qualcomm.lbat.Utils.MyExceptionHandler;
import com.qualcomm.lbat.Utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private String status;
    private Intent intent;
    private static boolean mainActivityRunning = false;
    private static final String TAG = "Main_Activity";
    private static boolean flag = false;
    private String parentFolder;
    private boolean dataAtSdCard=true;
    private Scenarios[] ScenariosList;
    private int scenariosCount;
    private ScenariosManager scenariosManager;
    private final SmarTestApplication smarTestApplication = SmarTestApplication.getInstance();
    private Util util;
    private String fileName;
    Handler  handler;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    private static MainActivity sInstance = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    // Getter to access Singleton instance
    public static MainActivity getInstance() {
        return sInstance ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        if(mainActivityRunning)
            return;

        handler = new Handler() ;
        util = new Util();
        util.setContext(smarTestApplication.getApplicationContext());

        try {
            Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        } catch (Exception e){
            Log.e(TAG,e.getMessage().toString());
        }

        if (getIntent().getBooleanExtra("crash", false)) {
//            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
            outputMessage("APP WAS CRASHED AND RESTARTED");
        }  else{
            outputMessage("REGULAR SMARTEST LAUNCH");
        }

        telephonyManager= util.getTelephonyManager();

        setContentView(R.layout.activity_main);

        status = "Welcome";
        scenariosCount = 0;
        ScenariosList = new Scenarios[5000];
        scenariosManager = new ScenariosManager();
        try {
            intent = new Intent(this, BroadcastService.class);
            registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
            // Provide a usable directory for temporary files
            System.setProperty("java.io.tmpdir",  getApplicationContext().getCacheDir().getAbsolutePath()); // line 3979
        }
        catch(Exception e) {
            Log.e(TAG,"Error:"+ e.toString());
        }



        status = "Checking configuration file";

        if(configFileCheckNew()==true) {
            if (dataAtSdCard){
                parentFolder = Environment.getExternalStorageDirectory().toString();
            }
            else {
                parentFolder = getFilesDir().getParentFile().toString();
            }
            if(!mainActivityRunning){
                mainActivityRunning = true;
                outputMessage("Waiting for few minutes");
                status = "Waiting for few minutes";
                util.unlock_screen(getApplicationContext());

                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        status = "Starting Services in : " + millisUntilFinished / 1000 + " seconds";
                    }

                    public void onFinish() {
                        status = "Services Launcing Started";
                    }
                }.start();

                util.unlock_screen(getApplicationContext());

                Timer waitTimer = new Timer();
                try {
                    waitTimer.schedule(new TimerTask() {
                        public void run() {
                            util.unlock_screen(getApplicationContext());
                            outputMessage("Starting SmarTest");
                            startServices();
                        }
                    }, 60000);
                }catch(Exception e){}
                flag = true;
            }  else {
                return;
            }
        }
        else {
            outputMessage(status);
        }

    }

    public void startServices() {

        for(int i=0;i<scenariosCount;i++){
            Scenarios scenario = ScenariosList[i];
            long time = System.currentTimeMillis();
            outputMessage("STARTSERVICES");
            int scenarioStatus =  scenariosManager.runScenario(ScenariosList[i],i, getApplicationContext());
            outputMessage("================================================");
            outputMessage("LIST : "+ScenariosList[i].getScenarioList());
            outputMessage("STATUS : "+ScenariosList[i].getSceanrioStatus());
            outputMessage("TIMER : "+ScenariosList[i].getIteraions());
            outputMessage("SCERANIO : "+ ScenariosList[i].serviceArr);
            outputMessage("SERVICE COUNT : "+ScenariosList[i].getServiceCount());
            outputMessage("================================================");
            outputMessage("SCENARIO STATUS : "+scenarioStatus);
            long curTime= System.currentTimeMillis();
            outputMessage("TOTAL TIME : "+(curTime-time));
            SystemClock.sleep(20000);

        }

        status = "All Services Launched";
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

    }

    public Runnable runnable = new Runnable() {

        public void run() {

            int Hours,Seconds, Minutes, MilliSeconds ;

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
//            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (MillisecondTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            Hours = Minutes / 60;
            Minutes = Minutes % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            status =   "SMARTEST RUNNING FOR : "+ Hours + ":" + Minutes + ":" + String.format("%02d", Seconds);
            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        try {
            startService(intent);
            registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
        }
        catch( Exception e) {
            Log.e("ReBrtctRcr Resume","Error:"+ e);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
            stopService(intent);
        }
        catch(Exception e) {
            Log.e("ReBrtctRcr Pause","Error:"+ e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            util.outputMessage(TAG,"DESTROYING MAIN ACTIVITY");
            unregisterReceiver(broadcastReceiver);
        }
        catch(Exception e) {
            Log.e(TAG,"Error: ON DESTROY"+ e);
        }
    }

    @SuppressLint("SdCardPath")
    public boolean configFileCheckNew() {

        File InputFile;
        try {
            String ConfigFile;
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                ConfigFile = "/sdcard/SmarTestConfig.map";
                InputFile = new File(ConfigFile);
                if(!InputFile.exists()) {
                    ConfigFile = getFilesDir().getParentFile().toString() + "/SmarTestConfig.map";
                    InputFile = new File(ConfigFile);
                    if(!InputFile.exists()) {
                        status = "Can�t find SmarTestConfig.map file";
                        return false;
                    }
                }
            }
            else {
                ConfigFile = getFilesDir().getParentFile().toString() + "/SmarTestConfig.map";
                InputFile = new File(ConfigFile);
                if(!InputFile.exists()) {
                    status = "Can�t find SmarTestConfig.map file";
                    return false;
                }
            }

            FileInputStream fIn = new FileInputStream(InputFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String fileLine;
            while((fileLine=myReader.readLine())!=null) {
                String[] splitwords = fileLine.split("\\s+");
                String word  = splitwords[0].trim();
                if(word.startsWith("start")){
                    ScenariosList[scenariosCount]= new Scenarios();
                    if(word.endsWith("S")){
                        ScenariosList[scenariosCount].setSceanrioStatus(Scenarios.SceanrioStatus.SERIES);
                        ScenariosList[scenariosCount].setIteraions(Integer.parseInt(splitwords[1].trim()));
                    }else if(word.endsWith("P")){
                        ScenariosList[scenariosCount].setSceanrioStatus(Scenarios.SceanrioStatus.PARALLEL);
                    }
                    String tmp="";
                    while((fileLine=myReader.readLine())!=null) {
                        if(fileLine.startsWith("end")){
                            break;
                        }
                        tmp+=fileLine;
                        tmp+="\n";
                    }
                    ScenariosList[scenariosCount].setScenarioList(tmp);
                    scenariosCount++;
                }
            }
            myReader.close();
            for(int i=0;i<scenariosCount;i++) {
                outputMessage("LIST : "+ScenariosList[i].getScenarioList());
                outputMessage("STATUS : "+ScenariosList[i].getSceanrioStatus());
                outputMessage("TIMER : "+ScenariosList[i].getIteraions());
            }
        }
        catch (IOException e) {
            Log.e("SmarTest", "Can�t find map file "+e.toString());
            status = "Can�t find map file " +e.toString();
            return false;
        }
        return true;
    }

    private void updateUI(Intent intent) {
        try {
            TextView statusValue = (TextView) findViewById(R.id.textView3);
            statusValue.setText(status);
        }
        catch(Exception e) {
            Log.e("regBrtcastReciver","Error:"+ e);
        }
    }

    public void outputMessage(String message)
    {
        try
        {
            Log.i(TAG,message);
            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }catch( Exception e)	{ }
    }
}
