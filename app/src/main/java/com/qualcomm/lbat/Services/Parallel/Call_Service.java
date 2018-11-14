package com.qualcomm.lbat.Services.Parallel;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.telephony.PreciseCallState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.qualcomm.lbat.Classes.Call;
import com.qualcomm.lbat.Classes.Conf;
import com.qualcomm.lbat.Classes.InCall;
import com.qualcomm.lbat.Classes.MoConfList;
//import com.qualcomm.lbat.Logging.CallLogResult;
import com.qualcomm.lbat.SmarTestApplication;
import com.qualcomm.lbat.Utils.Constants;
import com.qualcomm.lbat.Utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import com.android.internal.telephony.ITelephonyMSim;
public class Call_Service extends Service implements LocationListener {
    private Intent callIntent;
    private String phoneNumbers;
    private String sub1phoneNumbers;
    private String sub2phoneNumbers;
    private String[] phoneNumbersArray;
    private String[] sub1phoneNumbersArray;
    private String[] sub2phoneNumbersArray;
    private int timeBetCalls;
    private String timeRange;
    private int minTimeBetCalls;
    private int maxTimeBetCalls;
    private int callSubscription;
    private boolean isConferenceEnabled;
    private boolean vtDowngrade;
    private String confPhoneNumbers;
    private String sub1confPhoneNumber;
    private String sub2confPhoneNumber;
    private String curConfPhoneNumber;
    private TelephonyManager telephonyManager;
    private LocationManager locManager;
    private TelecomManager telecomManager;
    private SubscriptionManager subscriptionManager;
    private static final String TAG = "Call_Service";
    private static final String FRONT_CAM = "FRONT_CAM";
    private static final String REAR_CAM = "REAR_CAM";
    private String fileName;

    private boolean isInCallEnabled;
    private int vtTimer;
    private boolean vtEnabled;
    private int holdTimer;
    private int unholdTimer;
    private boolean holdEnabled;
    private boolean camToggleEnabled;
    private int muteTimer;
    private int unmuteTimer;
    private boolean muteEnabled;
    private int speakerOnTimer;
    private int speakerOffTimer;
    private boolean speakerEnabled;
    private int moCallTimer;
    private int moConfTimer;
    private int rearCamOnTimer;
    private int rearCamOffTimer;
    private int switchCallTimer;
    private boolean switchCallEnabled;
    private String switchCallNumber;
    private int switchCallCounts;
    private boolean switchCallNumAdded;
    private int switchCounter;
    private boolean switchinitated;
    private int switchCallTimeDiff;
    private int callCount;


    private static int LogIteration;
    private static String iter;
    private String SimSlot;
    private String confResult;
    private IBinder mbinder;
    private String parentFolder;
    private boolean callActive;
    private boolean moConfAdded;
    private boolean addCall;
    private boolean isCallVT;
    private boolean isVtVolteChanged;
    private boolean boolMoConfAction;

    private static int sim1Counter;
    private static int sim2Counter;
    private callState callState = new callState();
    private String vtVoltevalue = "";
    private boolean callDialing;
    private static boolean endCallInitiated = false;
    private Util util;
//    private CallLogResult callLogResult;
    private Call call;
    private InCall inCall;
    private MoConfList moConfList;
    private boolean SmarTestCall = false;
    private Camera mCamera;
    private FileInputStream fileInputStream;
    private CallTypes callType;
    private Location lastKnownLoc;
    private Location startCallLocation;
    private Location endCallLocation;
    public static Call_Service call_service;


    public Call_Service(){
        call_service = this;
    }

    final class callState extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle extras = intent.getExtras();
            int state = intent.getIntExtra(TelephonyManager.EXTRA_FOREGROUND_CALL_STATE, -2);
            try {
                String callState = Constants.HYPHEN;
                switch (state) {
                    case PreciseCallState.PRECISE_CALL_STATE_NOT_VALID:
                        callState = "NOT_VALID";
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_IDLE:
                        callState = "IDLE";
                        if(callDialing && !callActive){
                            //callLogResult.LogCallStates+=callState+":";
                            logEndResult(Constants.FAILURE);
                        }else if(callDialing && callActive){
                            //callLogResult.LogCallStates+=callState+":";
                            logEndResult(Constants.SUCCESS);
                        }
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_ACTIVE:
                        callState = "ACTIVE";
                        callActive = true;
                        if(SmarTestCall) {
                            manageCallActive();
                        }
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_HOLDING:
                        callState = "HOLDING ";
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_DIALING:
                        callState = "DIALING";
                        callDialing = true;
                        if(!callActive) {
                            logStartResult();
                        }
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_ALERTING:
                        callState = "ALERTING";

                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_INCOMING:
                        callState = "INCOMING";
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_WAITING:
                        callState = "WAITING";
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_DISCONNECTED:
                        callState = "DISCONNECTED";
                            if (callActive) {
                                //callLogResult.LogCallStates += callState + ":";
                                logEndResult(Constants.SUCCESS);
                            } else {
                                //callLogResult.LogCallStates += callState + ":";
                                logEndResult(Constants.FAILURE);
                        }
                        break;
                    case PreciseCallState.PRECISE_CALL_STATE_DISCONNECTING:
                        callState = "DISCONNECTING";
                            if (callActive) {
                                //callLogResult.LogCallStates += callState + ":";
                                logEndResult(Constants.SUCCESS);
                        }
                        break;
                }
                //callLogResult.LogCallStates+=callState+":";
                //util.outputMessage(TAG,"Call State for Number : " + callLogResult.LogCallNumber + " | State : " + callState);
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }
    }

    public enum CallTypes {
        REGULAR(-1),VOLTE(0),VT_TX(1),VT_RX(2),VT(3) ;

        private int ctValue;
        CallTypes(int value) { this.ctValue = value;} // Constructor
        public int id(){return ctValue;}                // Return enum index

        public static CallTypes fromId(int value) {
            for(CallTypes nwMode : values()) {
                if (nwMode.ctValue == value) {
                    return nwMode;
                }
            }
            return null;
        }
    }

    public void logEndResult(String logResult){
//        util.outputMessage(TAG,"CALL : "+logResult);
//        if (util.isApmOn()==1) {
//            callLogResult.LogAPMStatus = "APM ON";
//        } else {
//            callLogResult.LogAPMStatus = "APM OFF";
//        }
        //callLogResult.LogResult = logResult;
        //callLogResult.LogStopTime = util.getTime(":");
        //callLogResult.LogNetworkAfter = util.getNetworkType(Integer.parseInt(SimSlot));
        //callLogResult.LogGPSAfter = util.getGPS();
        //callLogResult.LogWifiStateAfter = util.getWifiState();
        //util.outputMessage(TAG,"CALL LOG : "+callLogResult.getCallLog());
//        if (fileName != null && !(callLogResult.LogScenarioIteration.equals(Constants.HYPHEN))) {
//            if (!new File(fileName).exists()) {
//                fileName = util.createLogFile(Constants.CALL, Constants.CALL_LOG_HEADER);
//                //util.writeHeading (fileName,Constants.CALL_LOG_HEADER);
//            }
//            util.writeDataToLogFile(fileName, callLogResult.getCallLog());
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(util.getContext()).edit();
//            editor.putInt("Call_Counter", LogIteration);
//            editor.commit();
//        }
        resetVariables();
    }

    public void logStartResult(){
        try {
            //callLogResult.LogScenarioIteration = iter;
            //callLogResult.LogStartTime = util.getTime(":");
            //callLogResult.LogNetworkBefore = util.getNetworkType(Integer.parseInt(SimSlot));
            //callLogResult.LogGPSBefore = util.getGPS();
            //callLogResult.LogWifiStateBefore = util.getWifiState();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    public void resetVariables(){
        callActive = callDialing = addCall = moConfAdded = isCallVT = isVtVolteChanged =
                endCallInitiated = holdEnabled = vtEnabled = muteEnabled  = speakerEnabled
                        = boolMoConfAction = SmarTestCall = camToggleEnabled = switchCallEnabled = switchCallNumAdded = switchinitated = false;
        vtVoltevalue = Constants.HYPHEN;
        //callLogResult.reset();
    }

    public void manageCallActive() {
        util.outputMessage(TAG,"In Call Manage Called");
        if(!endCallInitiated && !call.isLocBasedCall){
            endCallInitiated = true;
            invokeEndCall();
        }
        if(holdTimer!=0 && !holdEnabled){
            holdEnabled = true;
            invokeHoldUnhold();
        }
        if(muteTimer!=0 && !muteEnabled){
            muteEnabled = true;
            invokeMuteUnmute();
        }
        if(speakerOnTimer!=0 && !speakerEnabled){
            speakerEnabled = true;
            invokeSpeakerOnOff();
        }
        if(vtTimer!=0 && isCallVT && !isVtVolteChanged){
            isVtVolteChanged = true;
            invokeVtDowngrade();
        } else if(vtTimer!=0 && !isCallVT && !isVtVolteChanged) {
            invokeVtUpgrade();
        }
        if(call.isMoConf) {
            if (!moConfAdded && addCall) {
                moConfAdded= true;
                invokeAddToConference();
            } else if (!addCall) {
                addCall=true;
                invokeAddCall();
            }
        }
        if(switchCallTimer != 0 && switchCallCounts != 0 && !switchCallEnabled) {
            util.outputMessage(TAG,"New call for switch Initiated");
            switchCallEnabled = true;
            invokeSwitchCall();
        } else if(switchCallEnabled && switchCallNumAdded && !switchinitated) {
            util.outputMessage(TAG,"call switching Initiating");
            intiateCallSwitching();
        }

        String cmd1 = "rm -r /data/data/com.example.utils/result.txt";
        util.runCmd(cmd1);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getCallStatus(false);
            }
        }, 2000);


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Bundle b = intent.getBundleExtra("Location");
            lastKnownLoc = (Location) b.getParcelable("Location");
            if (lastKnownLoc != null) {
                util.outputMessage("Call And GPS Service", "Location Change recieved in Call "+lastKnownLoc);
                float startDistInMts = startCallLocation.distanceTo(lastKnownLoc);
                float endDistInMts = endCallLocation.distanceTo(lastKnownLoc);
                if(startDistInMts <=25 && !callDialing) {
                    callDialing = true;
                    initiateCallingProcess();
                }

                if(endDistInMts <= 35 && callActive) {
                    endCall();
                }
            }
        }
    };

    public void resetIncallTimer(){
        vtTimer = muteTimer = holdTimer = speakerOnTimer = moCallTimer = rearCamOnTimer = switchCallTimer = switchCallCounts = callCount = switchCounter = switchCallTimeDiff = 0;
    }

    public void getCallStatus(final boolean isCallEnd) {

        util.outputMessage(TAG,"get call staatus before end call"+SimSlot);
        String cmd2 = "am start -n com.example.utils/.PhoneCallAppActivity -e SUB "+SimSlot+" -e Get_Call_Type 3";
        util.runCmd(cmd2);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isCameraInUse();
                try {
                    getCallTypeStatusFromResultFile(isCallEnd);
                    util.outputMessage(TAG,"After 5 sec called");
                } catch (IOException e) {
                    util.outputMessage(TAG,"exception in getCallStatus");
                    e.printStackTrace();
                }
            }
        }, 10000);

    }

    public boolean isCameraInUse() {
        Log.d(TAG, "Camera Called");
        Camera c = null;
        try {
            c = Camera.open();
            Log.d(TAG,"Camera not in Use");
            //callLogResult.LogCallType = Constants.REGULAR;
        } catch (RuntimeException e) {
            //callLogResult.LogCallType = Constants.VT;
            if(rearCamOnTimer!=0 && !camToggleEnabled){
                camToggleEnabled = true;
                invokeCameraToggle();
            }
            Log.d(TAG,"Camera Already in Use");

            return true;
        } finally {
            if (c != null) c.release();
        }
        return false;
    }

    public void getCallTypeStatusFromResultFile(boolean isCallEnd) throws IOException {
        util.outputMessage(TAG,"start of  getCallTypeStatusFromResultFile");
        String path = Environment.getDataDirectory().getAbsolutePath()+"/data/com.example.utils/result.txt";
        util.outputMessage(TAG, "Path === "+path);
        File yourFile = new File(path);
        if(yourFile.length() > 0) {
            try {
                fileInputStream = new FileInputStream(yourFile);
                util.outputMessage(TAG,"file Input Stream created");
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getStackTrace().toString());
                util.outputMessage(TAG,"Exception in creating input Stream");
            }
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            util.outputMessage(TAG,"File length greater than 0");
            String fileContent;
            if (null != sb) {
                fileContent = sb.toString().trim();
                if (!TextUtils.isEmpty(fileContent)) {
                    util.outputMessage(TAG,"File content not empty"+fileContent);
                    String[] strArray = fileContent.split(";");
                    for (int i = 0; i < strArray.length; i++) {
                        String keyVal = strArray[i];
                        util.outputMessage(TAG,"File Array Val call type exist "+ Arrays.toString(strArray));
                        if(keyVal.contains("CALL TYPE")) {
                            String[] keyValArr = keyVal.split("=");
                            util.outputMessage(TAG,"Call Type Array "+ Arrays.toString(keyValArr));
                            callType = CallTypes.fromId(Integer.valueOf(keyValArr[1]));
                            if (!isCallEnd) {
                                //callLogResult.LogCallTypeBefore = callType.name();
                            } else {
                                //callLogResult.LogCallTypeAfter = callType.name();
                                delayedCallEnd();
                            }
                        } else {
                            logCallTypeAndEndCall(isCallEnd);
                        }
                    }
                } else {
                    logCallTypeAndEndCall(isCallEnd);
                }
            } else {
                logCallTypeAndEndCall(isCallEnd);
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "" + e.getStackTrace());
            }
        } else {
            logCallTypeAndEndCall(isCallEnd);
        }
    }

    public void logCallTypeAndEndCall(boolean isCallEnd){
        util.outputMessage(TAG,"File length = 0");
        if (!isCallEnd) {
            //callLogResult.LogCallTypeBefore = "-";
        } else {
            //callLogResult.LogCallTypeAfter = "-";
            delayedCallEnd();
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        LogIteration =0;
        sim1Counter =0;
        sim2Counter =0;
        switchCounter = 0;
        switchCallTimeDiff = 0;
        isCallVT = false;
        resetIncallTimer();
        //switchCallEnabled = switchInitiated =
        vtEnabled = muteEnabled = holdEnabled = speakerEnabled = camToggleEnabled = switchCallEnabled = switchCallNumAdded = false;
        try {
            util = new Util();
            util.setContext(getApplicationContext());
            telephonyManager= util.getTelephonyManager();
            locManager = util.getLocationManager();
            telecomManager = util.getTelecomManager();
            locManager.requestLocationUpdates("gps", 3000, 10, this);
            //callLogResult = new CallLogResult();

            util.runCmd("ps");
            startCallLocation = new Location("");
            endCallLocation = new Location("");
            fileName = util.getFilename(Constants.CALL);

            if(fileName!=null && !new File(fileName).exists()) {
                fileName = util.createLogFile(Constants.CALL, Constants.CALL_LOG_HEADER);
                //util.writeHeading(fileName, Constants.CALL_LOG_HEADER);
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        }catch(Exception e){
            e.printStackTrace();
            util.outputMessage(TAG,e.toString());
        }
        registerReceiver(callState, new IntentFilter("android.intent.action.PRECISE_CALL_STATE"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            util.outputMessage(TAG,"On Start Command Called : ");
            call = (Call) intent.getSerializableExtra("CallObj");
            inCall = (InCall) intent.getSerializableExtra("InCallObj");
            moConfList = (MoConfList) intent.getSerializableExtra("MoConfListObj");

            if(inCall!=null && inCall.enable){
                vtTimer = inCall.vtTimer;
                holdTimer = inCall.holdTimer;
                unholdTimer = inCall.unholdTimer;
                muteTimer =inCall.muteTimer;
                unmuteTimer = inCall.unmuteTimer;
                speakerOnTimer = inCall.speakerOnTimer;
                speakerOffTimer = inCall.speakerOffTimer;
                moCallTimer = inCall.moCallTimer;
                moConfTimer = inCall.moConfTimer;
                rearCamOnTimer = inCall.rearCameraOnTimer;
                rearCamOffTimer = inCall.rearCameraOffTimer;
                switchCallTimer = inCall.swCallTimer;
                switchCallNumber = inCall.swNumber;
                switchCallCounts = inCall.swCounts;
                switchCallTimeDiff = inCall.swTimeDiff;
            }  else {
                resetIncallTimer();
            }

            if(call.isLocBasedCall == true) {
                String [] srcLatLng = call.startCallLoc.split(",");
                startCallLocation.setLatitude(Double.parseDouble(srcLatLng[0]));
                startCallLocation.setLongitude(Double.parseDouble(srcLatLng[1]));

                String [] dstLatLng = call.endCallLoc.split(",");
                endCallLocation.setLatitude(Double.parseDouble(dstLatLng[0]));
                endCallLocation.setLongitude(Double.parseDouble(dstLatLng[1]));
            }

            String[] temp = call.timeRange.split(":");
            minTimeBetCalls = Integer.parseInt(temp[0]);
            maxTimeBetCalls = Integer.parseInt(temp[1]);

            phoneNumbersArray = call.number.split(":");

            if(phoneNumbersArray[0]!="0"){
                Log.i(TAG,"SUB 1 - "+phoneNumbersArray[0]);
                sub1phoneNumbers = phoneNumbersArray[0];
                sub1phoneNumbersArray = sub1phoneNumbers.split(",");
            }

            if(phoneNumbersArray[1]!="0"){
                Log.i(TAG, "SUB 2 - " + phoneNumbersArray[1]);
                sub2phoneNumbers = phoneNumbersArray[1];
                sub2phoneNumbersArray = sub2phoneNumbers.split(",");
            }

            if(call.isMoConf){
                String[] confTmp = call.moConfNumber.split(":");
                sub1confPhoneNumber = confTmp[0];
                sub2confPhoneNumber = confTmp[1];
            }

            if(!new File(fileName).exists()) {
                fileName = util.createLogFile(Constants.CALL, Constants.CALL_LOG_HEADER);
                //util.writeHeading(fileName, Constants.CALL_LOG_HEADER);
            }
            util.outputMessage(TAG,"FILENAME : "+fileName);
            mainloop();
        }
        catch(Exception e) {
            Log.e(TAG,e.toString());
        }
        return START_REDELIVER_INTENT;
    }

    public void invokeEndCall(){
        util.outputMessage(TAG,"*** Invoke End Call Called");
        long timeSpan=(long)(call.callDuration*1000);
        Timer timerCallEnd = new Timer();
        try {
            timerCallEnd.schedule(new TimerTask() {
                public void run() {
                    util.outputMessage(TAG,"Invoke End Call before");
                    endCall();
                    util.outputMessage(TAG,"Invoke End Call After");
                }
            }, timeSpan);
        }catch(Exception e){
            Log.e(TAG,e.toString());
        }
    }

    public void invokeHoldUnhold(){
            Timer timerCallHold = new Timer();
            try {
                timerCallHold.schedule(new TimerTask() {
                    public void run() {
                        InCallAction(Constants.HOLD, null);
                    }
                }, (holdTimer * 1000));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (unholdTimer != 0) {
                Timer timerCallUnhold = new Timer();
                try {
                    timerCallUnhold.schedule(new TimerTask() {
                        public void run() {
                            InCallAction(Constants.UNHOLD, null);
                        }
                    }, (unholdTimer * 1000));
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
            }
        }
    }

    public void invokeSwitchCall() {
        util.outputMessage(TAG,"invokeSwitchCall called");
        final Timer waitTimer = new Timer();
        try {
            waitTimer.schedule(new TimerTask() {
                public void run() {
                    if (callActive) {
                        util.outputMessage(TAG,"Switch call Initiated");
                        invokeCallToSwitchCallNumber();
                    } else {
                        waitTimer.cancel();
                    }
                }
            }, (switchCallTimer*1000));
        }catch(Exception e){}

    }

    public void intiateCallSwitching () {
        util.outputMessage(TAG,"intiateCallSwitching called");
        switchinitated = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callActive) {
                    if(switchCounter <= switchCallCounts) {
                        InCallAction(Constants.SWAP, null);
                        util.outputMessage(TAG, "Switch Call Called ***" + switchCounter);
                        switchCounter++;
                        handler.removeCallbacksAndMessages(null);
                        intiateCallSwitching();
                    } else {
                        util.outputMessage(TAG, "Call End Called ***" + switchCallTimer);
                        handler.removeCallbacksAndMessages(null);
                        for(int i =0; i<callCount; i++) {
                            endCall();
                        }
                    }
                } else {
                    handler.removeCallbacksAndMessages(null);
                }
            }
        }, switchCallTimeDiff * 1000);

    }

    public void invokeMuteUnmute(){
        Timer timerCallHold = new Timer();
        try {
            timerCallHold.schedule(new TimerTask() {
                public void run() {
                    InCallAction(Constants.MUTETOGGLE,null);
                }
            }, (muteTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        if(unmuteTimer!=0) {
            Timer timerCallUnhold = new Timer();
            try {
                timerCallUnhold.schedule(new TimerTask() {
                    public void run() {
                        InCallAction(Constants.MUTETOGGLE, null);
                    }
                }, (unmuteTimer * 1000));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void invokeCameraToggle() {
        //callLogResult.LogCameraStates = FRONT_CAM + " | ";
        Timer timerRearCameraOn = new Timer();
        try {
            timerRearCameraOn.schedule(new TimerTask() {
                public void run() {
                    InCallAction(Constants.CAMERATOGGLE,null);
                    //callLogResult.LogCameraStates += REAR_CAM + " | ";
                }
            }, (rearCamOnTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        if(rearCamOffTimer!=0) {
            Timer timerRearCameraOff = new Timer();
            try {
                timerRearCameraOff.schedule(new TimerTask() {
                    public void run() {
                        InCallAction(Constants.CAMERATOGGLE, null);
                        //callLogResult.LogCameraStates += FRONT_CAM;
                    }
                }, (rearCamOffTimer * 1000));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void invokeSpeakerOnOff(){
        Timer timerCallHold = new Timer();
        try {
            timerCallHold.schedule(new TimerTask() {
                public void run() {
                    InCallAction(Constants.SPEAKERTOGGLE,null);
                }
            }, (speakerOnTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        if(speakerOffTimer!=0) {
            Timer timerCallUnhold = new Timer();
            try {
                timerCallUnhold.schedule(new TimerTask() {
                    public void run() {
                        InCallAction(Constants.SPEAKERTOGGLE, null);
                    }
                }, (speakerOffTimer * 1000));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void invokeVtDowngrade(){
        vtVoltevalue ="0";
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    changeVTVolte(SimSlot,vtVoltevalue);
                }
            }, (vtTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void invokeVtUpgrade(){
        vtVoltevalue = "3";
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    changeVolteVT(SimSlot,vtVoltevalue);
                }
            }, (vtTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void invokeCallToSwitchCallNumber(){
        if (SimSlot.equals("0")) {
            util.outputMessage(TAG,"SUB 1");
            addCall(switchCallNumber, 0);
        }
        if (SimSlot.equals("1")) {
            util.outputMessage(TAG, "SUB 2");
            addCall(switchCallNumber, 1);
        }

        switchCallNumAdded = true;

    }


    public void invokeAddCall() {
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    if (SimSlot.equals("0")) {
                        util.outputMessage(TAG,"SUB 1");
                            addCall(sub1confPhoneNumber, 0);
                    }
                    if (SimSlot.equals("1")) {
                        util.outputMessage(TAG,"SUB 2");
                            addCall(sub2confPhoneNumber, 1);
                    }
                }
            }, (moCallTimer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void invokeAddToConference(){
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    addToConference(SimSlot);
                }
            }, ((moConfTimer-moCallTimer)*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void invokeFirstMoConfAction(int timer, final String number, final String moConfType){
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    if(moConfType.equals(Constants.MO_CONF_IMS)) {
                        addImsNumbertoConf(number);
                    } else if(moConfType.equals(Constants.MO_CONF_REGULAR)){
                        final SmarTestApplication globalClass = (SmarTestApplication) getApplicationContext();
                        startCall(number,globalClass.getActiveSubVoice());
                    }
                }
            }, (timer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void invokeSecondMoConfAction(int timer, final String number, final String moConfType){
        Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
                    if(moConfType.equals(Constants.MO_CONF_IMS)) {
                        removeImsNumberfromConf(number);
                    } else if(moConfType.equals(Constants.MO_CONF_REGULAR)){
                        final SmarTestApplication globalClass = (SmarTestApplication) getApplicationContext();
                        addToConference(String.valueOf(globalClass.getActiveSubVoice()));
                    }
                }
            }, (timer*1000));
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
    }

    public void changeVTVolte(String subId, String videostate){
        try {
            isVtVolteChanged = true;
            util.outputMessage(TAG,"VOLTE <-> VT");
            Runtime runtime = Runtime.getRuntime();
            String cmd = "am start -n com.example.utils/.PhoneCallAppActivity -e SUB "+subId+" -e Upgrade_Downgrade "+videostate;
            Process proc = runtime.exec(cmd);
        } catch (Exception e){

        }
    }

    public void changeVolteVT(String subId, String videostate) {
        try {
            isVtVolteChanged = true;
            util.outputMessage(TAG,"VOLTE <-> VT");
            Runtime runtime = Runtime.getRuntime();
            String cmd = "am start -n com.example.utils/.PhoneCallAppActivity -e SUB "+subId+" -e Upgrade_Downgrade "+videostate;
            Process proc = runtime.exec(cmd);
        } catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }

    public void addCall(String callNumber, int subId){
        try {
            util.outputMessage(TAG,"MAKING A NEW OUTGOING CALL : "+callNumber);
            startCall(callNumber,subId);
        }catch(Exception e){
            util.outputMessage(TAG,e.toString());
        }
    }

    public  void addToConference(String subId){
        try {
            util.outputMessage(TAG,"ADDING TO CONFERENCE");
            //callLogResult.LogCallStates += "CONFERENCE" + "|";
            String cmd = "am start -n com.example.utils/.ConferenceCall -e subscription " + subId;
            util.runCmd(cmd);
            if(moConfList!=null && moConfList.enable && !boolMoConfAction){
                boolMoConfAction =true;
                perfromMoConfAction();
            }
        } catch (Exception e){

        }
    }

    public void addImsNumbertoConf(String number){
        util.outputMessage(TAG,"ADDING IMS Number");
        //callLogResult.LogCallStates += "CONFERENCE" + "|";
        String cmd = "am start -n com.qualcomm.apt.imsutils/.ConferenceCallUtils -e testCaseName addparticipant -e callStatus FG -e telnumber "+number;
        util.runCmd(cmd);
    }

    public void removeImsNumberfromConf(String number){
        util.outputMessage(TAG,"Removing IMS Number");
        //callLogResult.LogCallStates += "Remove" + "|";
        String cmd = "am start -n com.qualcomm.apt.imsutils/.ConferenceCallUtils -e testCaseName removeparticipant -e callStatus FG -e telnumber "+number;
        util.runCmd(cmd);
    }

    public void perfromMoConfAction(){
        if(moConfList!=null) {
            for (Conf conf : moConfList.confList) {
                if (conf.firstTimer != 0) {
                    invokeFirstMoConfAction((conf.firstTimer - moConfTimer), conf.confNumber, conf.moConfCallTpye);
                    if (conf.secondTimer != 0) {
                        invokeSecondMoConfAction((conf.secondTimer - moConfTimer), conf.confNumber, conf.moConfCallTpye);
                    }
                }
            }
        }
    }

    public void mainloop() {
        if(call.isLocBasedCall == false) {
            initiateCallingProcess();
            nextIteration();
        }
    }

    public void initiateCallingProcess() {
        util.unlock_screen(getApplicationContext());

        resetVariables();
        util.outputMessage(TAG,"mainloop end call");
        endCall();
        int callCnt = PreferenceManager.getDefaultSharedPreferences(util.getContext()).getInt(
                "Call_Counter", 0);
        LogIteration = callCnt;
        //callLogResult.LogIteration = ++LogIteration;
        iter = "P"+LogIteration;
        String curPhoneNumber;
        util.outputMessage(TAG,"Initiating Call");
        if(call.subId==1){
            if(callCnt%2==0){
                if(!phoneNumbersArray[0].equals("0")){
                    curPhoneNumber = sub1phoneNumbersArray[sim1Counter % sub1phoneNumbersArray.length];
                    startCall(curPhoneNumber,0);
                    sim1Counter++;
                }else{
                    curPhoneNumber = sub2phoneNumbersArray[sim2Counter % sub2phoneNumbersArray.length];
                    startCall(curPhoneNumber,1);
                    sim2Counter++;
                }
            }else{
                if(!phoneNumbersArray[1].equals("0")) {
                    curPhoneNumber = sub2phoneNumbersArray[sim2Counter % sub2phoneNumbersArray.length];
                    startCall(curPhoneNumber,1);
                    sim2Counter++;
                }else{
                    curPhoneNumber = sub1phoneNumbersArray[sim1Counter % sub1phoneNumbersArray.length];
                    startCall(curPhoneNumber,0);
                    sim1Counter++;
                }
            }
            callCnt++;
        }else{
            util.outputMessage(TAG,"Only Single Sim");
            curPhoneNumber = sub1phoneNumbersArray[sim1Counter % sub1phoneNumbersArray.length];
            startCall(curPhoneNumber,0);
            sim1Counter++;
        }
    }

    public void nextIteration(){
        util.outputMessage(TAG,"Next iteration Called");
        Random rand = new Random();
        timeBetCalls = rand.nextInt((maxTimeBetCalls - minTimeBetCalls) + 1) + minTimeBetCalls;
        long timeSpan = (long)(call.callDuration*1000) + (long)(timeBetCalls*60000);
        final Timer timerCallStart = new Timer();
        try {
            timerCallStart.schedule(new TimerTask() {
                public void run() {
//                    if(!Constants.EMPTYSTRING.equals(callLogResult.LogCallNumber)){
//                        if(callActive){
//                            logEndResult(Constants.SUCCESS);
//                        }  else {
//                            logEndResult(Constants.FAILURE);
//                        }
//                    }
                    util.outputMessage(TAG,"MAKING NEXT ITERATION");
                    if(!((SmarTestApplication)getApplicationContext()).getIsXoShutdownEnabled()) {
                        mainloop();
                    } else {
                        timerCallStart.cancel();
                        timerCallStart.purge();
                    }
                }
            }, timeSpan);
        }catch(Exception e){}
    }

    public IBinder getMSIMService() {
        if (mbinder == null) {
            @SuppressWarnings("rawtypes")
            Class ServiceManager;
            try {
                ServiceManager = Class.forName("android.os.ServiceManager");
                @SuppressWarnings("unchecked")
                Method getServiceMethod = ServiceManager.getDeclaredMethod("getService", String.class);
                mbinder = (IBinder) getServiceMethod.invoke(ServiceManager,"phone_msim");
            }catch (Exception e) {

            }
        }
        return mbinder;
    }
    
    public void startCall(String curPhoneNumber, int callSubscription) {
        util.outputMessage(TAG,"Start Call Initiated");
        callCount++;
        try {
            SmarTestCall = true;
            Log.i(TAG,curPhoneNumber);
            Log.i(TAG, String.valueOf(callSubscription));
            final SmarTestApplication globalClass = (SmarTestApplication) getApplicationContext();
            globalClass.setActiveSubVoice(callSubscription+1);

            //callLogResult.LogCallNumber += curPhoneNumber +"|";
            //callLogResult.LogCallStates += curPhoneNumber + "|";
            //callLogResult.LogImsRegistered = String.valueOf(util.getImsRegisteredState());

            if(curPhoneNumber.toLowerCase().startsWith("vt")) {
                curPhoneNumber = curPhoneNumber.substring(2);
                Log.i(TAG,curPhoneNumber);
                callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", curPhoneNumber, null));
                callIntent.putExtra("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", VideoProfile.VideoState.BIDIRECTIONAL);
                isCallVT = true;

            } else {
                callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + curPhoneNumber));
                isCallVT = false;

            }

            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);

            if (Build.VERSION.SDK_INT >= 26) {
                if(call.subId==1) {
                    int t = callSubscription+1;
                    String cmd = "am start -n com.example.utils/.DualSIMSettingsActivity -e Voice " + String.valueOf(t);
                    util.outputMessage(TAG,cmd);
                    Process proc = Runtime.getRuntime().exec(cmd);
                    SystemClock.sleep(2000);
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                Method getSubIdMethod = SubscriptionManager.class.getDeclaredMethod("getSubId", int.class);
                getSubIdMethod.setAccessible(true);
                int subIdForSlot = ((int[]) getSubIdMethod.invoke(SubscriptionManager.class, callSubscription))[0];
                util.outputMessage(TAG,"SUBIDSLOT : "+subIdForSlot);
                ComponentName componentName = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
                PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, String.valueOf(subIdForSlot));
                callIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
//                SimSlot = String.valueOf(callSubscription);
            }
            //callLogResult.LogSimSlot = SimSlot = String.valueOf(callSubscription);
            startActivity(callIntent);
            util.outputMessage(TAG, "Call start");
        }
        catch(Exception e) {
            Log.e(TAG,"Error: "+e);
        }

    }

    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    public void endCall() {
        util.outputMessage(TAG,"End Call Called");
        if(callActive) {
            getCallStatus(true);
        }
    }


    public void delayedCallEnd(){
        callCount--;
        util.outputMessage(TAG,"End Call Initiated");
        try {
            String cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                    "-e usecase end_call -e filename usecase_callui.json " +
                    "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
            final SmarTestApplication globalClass = (SmarTestApplication) getApplicationContext();
            String carrier = telephonyManager.getNetworkOperatorName(globalClass.getActiveSubVoice());
            if(callActive) {
                if (telephonyManager == null || carrier.contains("Verizon")) {
                    Process proc = Runtime.getRuntime().exec("input keyevent KEYCODE_ENDCALL");
                    int value = proc.waitFor();
                    util.outputMessage(TAG, "KEYEVENT USED TO END CALL :" + value);
                    if (carrier.contains("Verizon")) {
                        Runtime.getRuntime().exec("input keyevent KEYCODE_HOME");
                    }
                    SystemClock.sleep(3000);
                } else {
                    telephonyManager.endCall();
                    util.outputMessage(TAG, "API SUCCESSFULLY ENDED CALL");
                }
            }

        }
        catch (Exception e) {
            Log.e(TAG,"Delayed End Call Error: "+e.toString());
        }
    }

    public void InCallAction(String action, String params[]){
        String cmd = "";
        int status=-1;
        try {
            switch (action) {
                case Constants.MUTETOGGLE:
                    //callLogResult.LogCallStates += "MUTE" + "|";
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase mute_toggle -e filename usecase_callui.json " +
                            "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
                    break;
                case Constants.HOLD:
                    //callLogResult.LogCallStates += "HOLD" + "|";
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase hold_call -e filename usecase_callui.json " +
                            "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
                    break;
                case Constants.UNHOLD:
                    //callLogResult.LogCallStates += "UNHOLD" + "|";
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase unhold_call -e filename usecase_callui.json " +
                            "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
                    break;
                case Constants.SPEAKERTOGGLE:
                    //callLogResult.LogCallStates += "SPEAKER" + "|";
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase speaker_toggle -e filename usecase_callui.json " +
                            "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
                    break;
                case Constants.CAMERATOGGLE:
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase toggle_camera -e filename usecase_callui.json " +
                            "com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
                case Constants.SWAP:
                    cmd = "am instrument -w -e class com.qualcomm.apt.uitester.UITest#runTest " +
                            "-e usecase swap_call -e filename usecase_callui.json " +
                            "-e video true com.qualcomm.apt.uitester.test/android.support.test.runner.AndroidJUnitRunner";
                    status = util.runCmd(cmd);
            }
            util.outputMessage(TAG, "INCALL ACTION : " + action + " | STATUS : " + String.valueOf(status));
        }catch(Exception e){
            Log.e(TAG,"Error: "+e.toString());
        }
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        try {
            util.outputMessage(TAG, "On Destroy Called in Call");
            unregisterReceiver(callState);
            resetIncallTimer();
            stopSelf();
            super.onDestroy();
        }  catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
    }

    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }

}

