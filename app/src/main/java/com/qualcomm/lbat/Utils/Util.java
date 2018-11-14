package com.qualcomm.lbat.Utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qualcomm.lbat.Controller.ScenariosManager;
import com.qualcomm.lbat.R;
import com.qualcomm.lbat.SmarTestApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shrishn on 8/28/2017.
 */

public class Util {

    private static final String TAG = "UTIL";
    private TelephonyManager telephonyManager;
    private Context context;
    private LocationManager locationManager;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private TelecomManager telecomManager;
    private PowerManager powerManager;
    private static String smarTestTimestamp;

    private String networkModeSub1;
    private String networkModeSub2;

    public void Util(){}

    public void setContext(Context context){
        this.context = SmarTestApplication.getInstance().getBaseContext();
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        telephonyManager= (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        telecomManager = (TelecomManager) this.context.getSystemService(Context.TELECOM_SERVICE);
        powerManager = (PowerManager) SmarTestApplication.getInstance().getBaseContext().getSystemService(Context.POWER_SERVICE);
    }

    public Context getContext(){
        return this.context;
    }

    public TelephonyManager getTelephonyManager(){
        return this.telephonyManager;
    }

    public LocationManager getLocationManager() {return this.locationManager ; }

    public WifiManager getWifiManager(){ return this.wifiManager; }

    public ConnectivityManager getConnectivityManager() { return this.connectivityManager; }

    public TelecomManager getTelecomManager() { return  this.telecomManager; }

    public void serviceDoneBroadcast(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ScenariosManager.mBroadcastStringServiceComplete);
        context.sendBroadcast(broadcastIntent);
        outputMessage(TAG,"SENDBROADCAST");
    }

    public void outputMessage(String TAG, final String message) {
        try {
            Log.i(TAG,message);
        }catch( Exception e){
            Log.e(TAG, e.toString());
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }

    public int runCmd(String cmd){
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmd);
            proc.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                outputMessage(TAG,"*******"+line);
            }
            in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = in.readLine()) != null) {
                outputMessage(TAG,"ERROR : " + line);
            }
            return proc.exitValue();
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
         return -1;
    }

    public boolean isPhoneAlive(){
        try{
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("pidof -s com.android.phone");
            proc.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                outputMessage(TAG,line);
            }
            in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = in.readLine()) != null) {
                outputMessage(TAG,"ERROR : " + line);
            }
        }  catch(Exception e){
            Log.e(TAG, e.toString());
        }
        return false;
    }

    public int isApmOn(){
        int apmStatus =0;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("settings get global airplane_mode_on");
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                 apmStatus = Integer.parseInt(line.trim());
            }
            in.close();
        } catch(Exception e){
            Log.e(TAG, e.toString());
        }
        return apmStatus;
    }

    public String getNetworkType(int subId) {
        try {
            String LogCellInfo = "-";
            String LogCellStrength = "-";
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("getprop gsm.network.type");
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            String nwMode="";
            while ((line = in.readLine()) != null) {
                String temp[] = null;
                if(line.contains(",")) {
                    temp = line.split(",");
                    nwMode= temp[subId];
                    if("Unknown".equals(nwMode)){
                       nwMode = getNetworkTypeApi(subId);
                    }
                }
                else {
                    nwMode = line.trim();
                    if("Unknown".equals(nwMode)){
                        nwMode = getNetworkTypeApi(subId);
                    }
                }

                int strengthDbm=0;
                List<CellInfo> all = telephonyManager.getAllCellInfo();
                outputMessage("telephonyManager", "The result is :- "+all);
                if((all != null) && !all.isEmpty()) {
                    int i=0;
                    CellInfo ci = all.get(0);
                    if (ci instanceof CellInfoLte) {
                        CellInfoLte cellinfo = (CellInfoLte)ci;
                        CellSignalStrengthLte cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
                        strengthDbm = cellSignalStrengthGsm.getDbm();
                        LogCellInfo = cellinfo.getCellIdentity().toString();
                    } else if (ci instanceof CellInfoWcdma) {
                        CellInfoWcdma cellinfo = (CellInfoWcdma) ci;
                        CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
                        strengthDbm = cellSignalStrengthGsm.getDbm();
                        LogCellInfo = cellinfo.getCellIdentity().toString();
                    } else if (ci instanceof CellInfoGsm) {
                        CellInfoGsm cellinfo = (CellInfoGsm) ci;
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
                        strengthDbm = cellSignalStrengthGsm.getDbm();
                        LogCellInfo = cellinfo.getCellIdentity().toString();
                    } else if (ci instanceof CellInfoCdma) {
                        CellInfoCdma cellinfo = (CellInfoCdma) ci;
                        CellSignalStrengthCdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
                        strengthDbm = cellSignalStrengthGsm.getDbm();
                        LogCellInfo = cellinfo.getCellIdentity().toString();
                    }
                    LogCellStrength = String.valueOf(strengthDbm);
                }
            }
            in.close();
            if("Unknown".equals(nwMode)){
                if(isApmOn()==1) {
                    nwMode = "APM";
                } else {
                    if("-".equals(LogCellInfo)){
                        nwMode = "OOS";
                    } else {
                        nwMode = "Not-Camped";
                    }
                }
            }
            return nwMode;
        } catch(Exception e){
            Log.e(TAG, e.toString());
            return "ERROR";
        }
    }

    @SuppressWarnings("deprecation")
    public void unlock_screen(Context context){
        try {
            outputMessage(TAG,"SCREEN ON = "+powerManager.isScreenOn());
            if( !powerManager.isScreenOn() ) {
                outputMessage(TAG,"SCREEN IS LOCKED.UNLOCKING NOW!");
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                wakeLock.acquire();

                KeyguardManager keyguardManager = (KeyguardManager)  SmarTestApplication.getInstance().getBaseContext().getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                keyguardLock.disableKeyguard();

                wakeLock.release();

                runCmd("input keyevent 82");
            }
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public void lock_screen(){
//        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        devicePolicyManager.lockNow();
        int status = runCmd("input keyevent 26");
        outputMessage(TAG,"LOCK SCREEN STATUS"+status);
    }
    public GpsCordinates getGPS() {
        GpsCordinates gps = new GpsCordinates();
        try {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                Location loc = locationManager.getLastKnownLocation("gps");
                if (loc != null) {
                    gps.setLatitude("" + loc.getLatitude());
                    gps.setLongitude("" + loc.getLongitude());
                    gps.setAltitude("" + loc.getAltitude());
                } else {
                    gps.setLatitude("No GPS");
                    gps.setLongitude("No GPS");
                    gps.setAltitude("No GPS");
                }
            }
        }
        catch( SecurityException e) {
            gps.setLatitude("No GPS");
            gps.setLongitude("No GPS");
        }
        return gps;
    }

    public void getCurrentNetworkMode(int SubId) {
        try {
            String line;
            outputMessage("Network Mode","Getting NW Mode");
            Runtime runtime = Runtime.getRuntime();
            String cmd = "am start -n com.example.utils/.NetworkModeActivity -e SUB " + SubId + " -e network_get_request dummy";
            outputMessage("network mode", cmd);
            Process proc = runtime.exec(cmd);
            Log.d(TAG,"networkType : "+proc.getInputStream());
            InputStream stdout = proc.getInputStream();
            InputStream stderr = proc.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                Log.d("[Output]", line);
            }
            br.close();

            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e("[Error]", line);
            }
            br.close();

        } catch (Exception e) {
            Log.e(TAG, "Error : " + e);
        }
    }
    
    public enum NetworkMode {
        GSM_ONLY(1), WCDMA_ONLY(2), GSM_WCDMA_AUTO(3), CDMA_EVDO_AUTO(4), CDMA_NO_EVDO(5), EVDO_NO_CDMA(6),
        GLOBAL(7), LTE_CDMA_EVDO(8), LTE_GSM_WCDMA(9), LTE_CMDA_EVDO_GSM_WCDMA(10), LTE_ONLY(11), LTE_WCDMA(12),
        TD_SCDMA_ONLY(13), TD_SCDMA_WCDMA(14), TD_SCDMA_LTE(15), TD_SCDMA_GSM(16), TD_SCDMA_GSM_LTE(17), TD_SCDMA_GSM_WCDMA(18),
        TD_SCDMA_WCDMA_LTE(19), TD_SCDMA_GSM_WCDMA_LTE(20), TD_SCDMA_CDMA_EVDO_GSM_WCDMA(21), TD_SCDMA_LTE_CDMA_EVDO_GSM_WCDMA(22);

        public int mValue;
        NetworkMode(int value) { this.mValue = value;} // Constructor
        public int id(){return mValue;}                // Return enum index

        public static NetworkMode fromId(int value) {
            for(NetworkMode color : values()) {
                if (color.mValue == value) {
                    return color;
                }
            }
            return null;
        }
    }

    public enum Result {
        SUCCESS(1), FAILURE(2);

        public int mValue;
        Result(int value) { this.mValue = value;}       // Constructor
        public int id(){return mValue;}                 // Return enum index

        public static Result fromId(int value) {
            for(Result status : values()) {
                if (status.mValue == value) {
                    return status;
                }
            }
            return null;
        }
    }

    public String getResultString(Result value){
        return String.valueOf(Util.Result.fromId(value.mValue));
    }

    public String getTime(String splChar){
        Calendar c = Calendar.getInstance();
        return  "" + c.get(Calendar.HOUR_OF_DAY) +splChar+c.get(Calendar.MINUTE)+splChar+ c.get(Calendar.SECOND);
    }

    public void setSmarTestTimestamp(String ts ){
        this.smarTestTimestamp = ts;
    }

    public String getSmarTestTimestamp(){
        return this.smarTestTimestamp;
    }

    public String getWifiState(){
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
       return wifi.getState().toString();
    }

    public String getDate(){
        Calendar c = Calendar.getInstance();
        return  "" + c.get(Calendar.YEAR) +"_"+(c.get(Calendar.MONTH)+1)+"_"+ c.get(Calendar.DATE);
    }

    public boolean getImsRegisteredState(){
        return telephonyManager.isImsRegistered();
    }

    /**
     * Get a hidden method instance from a class
     * @param methodName The name of the method to be taken from the class
     * @param fromClass The name of the class that has the method
     * @return A Method instance that can be invoked
     */
    public Method getHiddenMethod(String methodName, Class fromClass, Class[] params) {
        Method method = null;
        try {
            Class clazz = Class.forName(fromClass.getName());
            method = clazz.getMethod(methodName, params);
            method.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return method;
    }


    public void setVoiceSubID(int callSubscriptionID){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Method m = null;
                Class subManagerClass = Class.forName("android.telephony.SubscriptionManager");
                m = subManagerClass.getMethod("from", Context.class);
                m.setAccessible(true);
                Object obj = m.invoke(null, this.context);
                Method getSubIdMethod = SubscriptionManager.class.getDeclaredMethod("getSubId", int.class);
                getSubIdMethod.setAccessible(true);
                int subIdForSlot = ((int[]) getSubIdMethod.invoke(SubscriptionManager.class, callSubscriptionID))[0];
                m = subManagerClass.getMethod("setDefaultVoiceSubId", Integer.TYPE);
                m.setAccessible(true);
                m.invoke(obj, subIdForSlot);
            }
        } catch (Exception e){
            Log.i(TAG, "Error : " + e.getCause());
        }
    }

    public void dataToggle(int mode, int subId){
        String cmd = "am start -n com.example.utils/.ToggleMobileData -e MODE "+ String.valueOf(mode)+" -e subscription "+ String.valueOf(subId);
        outputMessage(TAG,cmd);
        runCmd(cmd);
    }

    public void subOnOff(String option, int subId){
        String cmd = "am start -n com.qualcomm.apt.simutils/.SIMUtils -e option "+option+" -e subscription "+ String.valueOf(subId);;
        outputMessage(TAG,cmd);
        runCmd(cmd);
    }

    public String getSmarTestVersion(){
        return this.context.getString(R.string.app_version_num);
    }

    public String getFilename(String serviceName){
        String filename = "";
        try {
            String root = Constants.SDCARD + "/logs/";
            filename = root +  getDate() + "_" + Build.SERIAL + "_" + getSmarTestVersion() + "_" + serviceName + ".csv" ;
        }
        catch(Exception e) {
            return null;
        }
        outputMessage(TAG,"FILENAME : "+filename);
        return filename;
    }
    public String createLogFile(String serviceName, String serviceLogHeader) {
        String filename = "";
        try {
            String root = Constants.SDCARD + "/logs/";
            filename = root +  getDate()+ "_" + Build.SERIAL + "_" + getSmarTestVersion() + "_" + serviceName + ".csv" ;

            File dir = new File(root);
            if(!dir.exists()) {
                if(!dir.mkdirs()) {
                    return null;
                }
            }

            File out = new File(filename);
            if(!out.exists()) {
                out.createNewFile();
                writeHeading (filename,serviceLogHeader);
            }
            if(serviceName.equals(Constants.SHUTDOWN)) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
                editor.putString(Constants.SHUTDOWN_LOG_PATH, filename);
                editor.commit();
            }
        }
        catch(Exception e) {
            return null;
        }
        outputMessage(TAG,"FILENAME : "+filename);
        return filename;
    }

    public void writeHeading(String filename, String data) {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(filename,true));
            out.write(data);
            out.newLine();
            out.close();
        }
        catch (Exception e) {
            Log.e(TAG,"Error: "+e);
        }
    }

    public void writeDataToLogFile(String filename, String data) {
        outputMessage(TAG,"Writing to FileNAME : "+filename);
        outputMessage(TAG,"DATA : "+data);
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(filename,true));
            out.write(data);
            out.newLine();
            out.close();
        }
        catch (Exception e) {
            Log.e(TAG,"Error: "+e);
        }
    }

   public String getResultTxt(){
        String result="";
        try {
            FileInputStream fIn = new FileInputStream(new File("/data/data/com.example.utils/result.txt"));
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String fileLine = null;
            while((fileLine=myReader.readLine())!=null) {
                result+=fileLine;
            }
        } catch(Exception e){
            Log.e(TAG,e.toString());
        }
        return  result;
    }


    public String deleteResultTxt(){
        String result="";
        try {
            File resultTxt = new File("/data/data/com.example.utils/result.txt");
            resultTxt.delete();
        } catch(Exception e){
            Log.e(TAG,e.toString());
        }
        return  result;
    }


    public String getNetworkTypeApi(int subId) {
        try {
            int type = telephonyManager.getNetworkType(subId);
            switch(type) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "Unknown";

                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";

                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";

                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";

                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";

                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";

                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";

                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPAP";

                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";

                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";

                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO_A";

                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";

                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";

                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "IDEN";

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";

                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "EHRPD";
                default:
                    return "-";
            }
        }catch(Exception e){return "";}
    }

//    public HashMap getNetworkTypeForSub(int SubId) {
//
//        String LogTime;
//        String LogNetworkSim1 = "-";
//        String LogNetworkSim2 = "-";
//        HashMap<String,String> iratHashmapSim = new HashMap<String,String>();
//        String LogCellInfo = "-";
//        String LogCellStrength = "-";
//
//        try {
//            Runtime runtime = Runtime.getRuntime();
//            Process proc = runtime.exec("getprop gsm.network.type");
//            Calendar c = Calendar.getInstance();
//            LogTime = "" + c.get(Calendar.HOUR_OF_DAY) +":"+c.get(Calendar.MINUTE)+":"+ c.get(Calendar.SECOND);
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            String line;
//
//            while ((line = in.readLine()) != null) {
//                String temp[] = null;
//                outputMessage("Util","While Line :--- "+line);
//                if(line.contains(",")) {
//                    temp = line.split(",");
//                    LogNetworkSim1 = temp[0].trim();
//                    LogNetworkSim2 = temp[1].trim();
//                    outputMessage("Util Msg In While loop", "The Nw1 : "+LogNetworkSim1 + " Nw2 : "+LogNetworkSim2);
//                    if("Unknown".equals(LogNetworkSim1) && "Unknown".equals(LogNetworkSim2)){
//                        if(isApmOn()==1) {
//                            LogNetworkSim1 = LogNetworkSim2 = "APM";
//                        }else{
//                            LogNetworkSim1 = getNetworkTypeApi(1).trim();
//                            LogNetworkSim2 = getNetworkTypeApi(2).trim();
//                        }
//                    }
//                    int strengthDbm=0;
//                    List<CellInfo> all = telephonyManager.getAllCellInfo();
//                    outputMessage("telphony Mgr", "The result is :- "+all);
//                    if((all != null) && !all.isEmpty()) {
//                        int i=0;
//                        CellInfo ci = all.get(0);
//                        if (ci instanceof CellInfoLte) {
//                            CellInfoLte cellinfo = (CellInfoLte)ci;
//                            CellSignalStrengthLte cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoWcdma) {
//                            CellInfoWcdma cellinfo = (CellInfoWcdma) ci;
//                            CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoGsm) {
//                            CellInfoGsm cellinfo = (CellInfoGsm) ci;
//                            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoCdma) {
//                            CellInfoCdma cellinfo = (CellInfoCdma) ci;
//                            CellSignalStrengthCdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        }
//                        LogCellStrength = String.valueOf(strengthDbm);
//                    }
//                    if("Unknown".equals(LogNetworkSim1)){
//                        if("-".equals(LogCellInfo)){
//                            LogNetworkSim1 = "OOS";
//                        } else {
//                            LogNetworkSim1 = "Not-Camped";
//                        }
//                    }
//
//                    if("Unknown".equals(LogNetworkSim2)){
//                        if("-".equals(LogCellInfo)){
//                            LogNetworkSim2 = "OOS";
//                        } else {
//                            LogNetworkSim2 = "Not-Camped";
//                        }
//                    }
//                    outputMessage("Util Msg In While loop", "The Nw1 : "+LogNetworkSim1 + " Nw2 : "+LogNetworkSim2);
//                }
//                else {
//                    LogNetworkSim1 = line.trim();
//                    if("Unknown".equals(LogNetworkSim1)){
//                        if(isApmOn()==1) {
//                            LogNetworkSim1 = "APM";
////                            SystemClock.sleep(15);
//                        } else{
//                            LogNetworkSim1 = getNetworkTypeApi(1);
//                        }
//                    }
//                    outputMessage("Util Msg In While loop 2", "The Nw1 : "+LogNetworkSim1 + " Nw2 : "+LogNetworkSim2);
//                    int strengthDbm=0;
//                    List<CellInfo> all = telephonyManager.getAllCellInfo();
//                    outputMessage("telphony Mgr", "The result is :- "+all);
//                    if((all != null) && !all.isEmpty()) {
//                        int i=0;
//                        CellInfo ci = all.get(0);
//                        if (ci instanceof CellInfoLte) {
//                            CellInfoLte cellinfo = (CellInfoLte)ci;
//                            CellSignalStrengthLte cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoWcdma) {
//                            CellInfoWcdma cellinfo = (CellInfoWcdma) ci;
//                            CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoGsm) {
//                            CellInfoGsm cellinfo = (CellInfoGsm) ci;
//                            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        } else if (ci instanceof CellInfoCdma) {
//                            CellInfoCdma cellinfo = (CellInfoCdma) ci;
//                            CellSignalStrengthCdma cellSignalStrengthGsm = cellinfo.getCellSignalStrength();
//                            strengthDbm = cellSignalStrengthGsm.getDbm();
//                            LogCellInfo = cellinfo.getCellIdentity().toString();
//                        }
//                        LogCellStrength = String.valueOf(strengthDbm);
//                    }
//                    if("Unknown".equals(LogNetworkSim1)){
//                        if("-".equals(LogCellInfo)){
//                            LogNetworkSim1 = "OOS";
//                        } else {
//                            LogNetworkSim1 = "Not-Camped";
//                        }
//                    }
//
//                }
//
//                outputMessage("Util Msg", "The Nw1 : "+LogNetworkSim1 + " Nw2 : "+LogNetworkSim2+" LogCellInfo : "+LogCellInfo+" LogCellStrength : "+LogCellStrength);
//
//                iratHashmapSim.put("NetworkSim0", LogNetworkSim1);
//                iratHashmapSim.put("NetworkSim1", LogNetworkSim2);
//                iratHashmapSim.put("LogCellInfo", LogCellInfo);
//                iratHashmapSim.put("LogCellStrength", LogCellStrength);
//            }
//
//            in.close();
//
//        }catch (Exception e){
//            outputMessage(TAG,e.toString());
//        }
//
//        outputMessage("Util for nework type", "The network type HASH MAP --- "+iratHashmapSim);
//
//        return iratHashmapSim;
//    }
}


