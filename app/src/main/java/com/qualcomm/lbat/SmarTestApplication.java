package com.qualcomm.lbat;

import android.app.Application;
import android.content.Context;

/**
 * Created by shrishn on 11/8/2017.
 */

public class SmarTestApplication extends Application {

    public static SmarTestApplication instance;
    private int activeSubVoice;
    private int activeSubData;
    private int activeSubSms;
    private String currentTimestamp;
    private boolean isPlmnSearchActive;
    private boolean isMmsServiceActive;
    private boolean isXoShutdownEnabled;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static SmarTestApplication getInstance() {
        return instance;
    }

    public boolean getIsPlmnSearchActive(){return this.isPlmnSearchActive;}

    public void setIsPlmnSearchActive(boolean isPlmnActive) {this.isPlmnSearchActive = isPlmnActive; }

    public boolean getIsMmsServiceActive(){return this.isMmsServiceActive;}

    public void setIsMmsServiceActive(boolean isMmsActive) { this.isMmsServiceActive = isMmsActive;}

    public int getActiveSubVoice(){
        return this.activeSubVoice;
    }

    public void setActiveSubVoice(int subId){
        this.activeSubVoice=subId;
    }

    public int getActiveSubData(){
        return this.activeSubData;
    }

    public void setActiveSubData(int subId){
        this.activeSubData=subId;
    }

    public int getActiveSubSms(){
        return this.activeSubSms;
    }

    public void setActiveSubSms(int subId){
        this.activeSubSms=subId;
    }

    public void setCurrentTimestamp(String timestamp){
        this.currentTimestamp = timestamp;
    }

    public String getCurrentTimestamp(){
        return this.currentTimestamp;
    }

    public void setIsXoShutdownEnabled(boolean isXoShutdown) {this.isXoShutdownEnabled = isXoShutdown; }

    public boolean getIsXoShutdownEnabled(){return this.isXoShutdownEnabled; }

}
