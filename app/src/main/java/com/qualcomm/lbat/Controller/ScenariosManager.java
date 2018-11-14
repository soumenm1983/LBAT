package com.qualcomm.lbat.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.qualcomm.lbat.Classes.Call;
import com.qualcomm.lbat.Classes.InCall;
import com.qualcomm.lbat.Classes.MoConfList;
import com.qualcomm.lbat.Classes.ServiceAction;
import com.qualcomm.lbat.Services.Common.GPS_Service;
import com.qualcomm.lbat.Services.Parallel.Call_Service;
import com.qualcomm.lbat.Utils.Util;

import java.io.Serializable;

/**
 * Created by shrishn on 8/22/2017.
 */

public class ScenariosManager extends BroadcastReceiver {
    public static final String mBroadcastStringServiceComplete = "com.qualcomm.SmarTest.Service_COMPLETED";
    private static Boolean boolserviceDone = false;
    private static Scenarios scenario;
    private static int iterationCounter=0;
    private static int serviceCounter=0;
    private static int smsCounter = 0;
    private static int callCounter = 0;
    private static final String TAG ="ScenariosManager";
    private final Object lock = new Object();
    private Util util;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(mBroadcastStringServiceComplete)) {
            outputMessage("SERVICE DONE");
            synchronized(boolserviceDone){
                boolserviceDone.notify();
            }
        }
    }

    public ScenariosManager(){
        util = new Util();
    }

    public int runScenario(Scenarios scenario, int ScenariosNumber, Context context){
        outputMessage("RUNSCENARIOS");
        this.scenario = scenario;
        util.setContext(context);
        String lines[] = scenario.getScenarioList().split("\n");

        for(int i =0; i <lines.length; i++){
            try {
                assignServices(lines[i], scenario, i);
            } catch ( Exception e){
                //Log.e(TAG,"ASSIGN SERVICES | LINE : "+i+" ERROR : "+e.toString());
                //Log.e(TAG,"ASSIGN SERVICES | SCENARIO : "+scenario.serviceArr[i]+" ERROR : "+e.toString());
            }
        }
        if(scenario.getSceanrioStatus()== Scenarios.SceanrioStatus.SERIES) {
            //seriesServices = new SeriesServices();
            util.outputMessage(TAG,"The iteration Coount --- "+scenario.getIteraions());
            util.outputMessage(TAG,"The iter --- "+iterationCounter);
            while(iterationCounter<scenario.getIteraions()){
                while(serviceCounter<scenario.getServiceCount()){
                    util.outputMessage(TAG,"The Service Count *** "+scenario.getServiceCount());
                    util.outputMessage(TAG,"The Service iter *** "+serviceCounter);
                    ServiceAction o = (ServiceAction)scenario.serviceArr[serviceCounter];
                    util.outputMessage(TAG,"Service Class"+ ((ServiceAction)scenario.serviceArr[serviceCounter]).getClass().getSimpleName().toString());
                    util.outputMessage(TAG,"SERVICE NAME : "+((ServiceAction)scenario.serviceArr[serviceCounter]).name);
                    util.outputMessage(TAG,"SERVICE ENABLE  : "+((ServiceAction)scenario.serviceArr[serviceCounter]).enable);
                    util.outputMessage(TAG,"SCENARIO : " + (ScenariosNumber+1) + " | ITERATION : " + (iterationCounter+1) + " | SERVICE : " + (serviceCounter+1));
                    String t = "S"+(ScenariosNumber+1)+"I"+(iterationCounter+1)+"S"+(serviceCounter+1);
                    if(o.enable) {
                        //runServiceInSeries(scenario.serviceArr[serviceCounter],t, context);
                        try {
                            synchronized (boolserviceDone) {
                                boolserviceDone.wait();
                                SystemClock.sleep(2000);
                            }
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
                        }
                    }
                    util.outputMessage(TAG,"Service exe done");
                    serviceCounter++;
                }
                serviceCounter=0;
                iterationCounter++;
            }
            iterationCounter    = 0;
            serviceCounter      = 0;
            smsCounter = 0;
            callCounter = 0;
            //util.outputMessage(TAG,"SERVICE DESTROYED : "+status);
            SystemClock.sleep(10000);
        } else if(scenario.getSceanrioStatus()== Scenarios.SceanrioStatus.PARALLEL){
            while(serviceCounter<scenario.getServiceCount()){
                ServiceAction o = (ServiceAction)scenario.serviceArr[serviceCounter];
                util.outputMessage(TAG,"Service Class Name : "+o.getClass().getSimpleName());
                util.outputMessage(TAG,"CHECKING SERVICE : "+o.name);
                if(o.enable) {
                    runServiceInParallel(scenario.serviceArr[serviceCounter], context);
                    SystemClock.sleep(5000);
                }
                serviceCounter++;
            }
        }
        return 1;
    }

    public void runServiceInParallel(Object o, Context context){
        if(o instanceof Call) {
            try {
                Intent callIntent = new Intent(context, Call_Service.class);
                callIntent.putExtra("CallObj",(Serializable) o);
                if(scenario.serviceArr[serviceCounter+1] instanceof InCall){
                    Object ic = scenario.serviceArr[serviceCounter+1];
                    serviceCounter++;
                    if(ic !=null) {
                        callIntent.putExtra("InCallObj", (Serializable) ic);
                    }
                }

                if(scenario.serviceArr[serviceCounter+1] instanceof MoConfList){
                    Object mo = scenario.serviceArr[serviceCounter+1];
                    serviceCounter++;
                    if(mo !=null) {
                        callIntent.putExtra("MoConfListObj", (Serializable) mo);
                    }
                }
                util.outputMessage(TAG, "start Call Service");
                context.startService(callIntent);
            }
            catch(Exception e) {
                Log.e(TAG, "FAILED TO START CALL SERVICE IN PARALLEL");
                Log.e(TAG,e.toString());
            }
        }
    }



    public void outputMessage(String message) {
        util.outputMessage(TAG,message);
    }


    public void assignServices(String line, Scenarios scenario, int i){
        String words[] = line.split("\\s+");
        String service = words[0];
        outputMessage("SWITCH CASE : "+service);
        switch (service){
            case "Call":
                Call c = new Call(words);
                scenario.serviceArr[i]=c;
                break;
            case "InCall":
                InCall ic = new InCall(words);
                scenario.serviceArr[i] = ic;
                break;
            default:
                throw new IllegalArgumentException("Invalid Service : " + service);
        }
    }

}




