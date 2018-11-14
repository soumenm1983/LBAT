package com.qualcomm.lbat.Classes;

import java.io.Serializable;

/**
 * Created by shrishn on 9/14/2017.
 */

public class Call extends ServiceAction implements Serializable {
    public String timeRange;
    public String number;
    public int callDuration;
    public int  subId;
    public String moConfNumber;
    public boolean isMoConf;
    public boolean mtConf;
    public boolean isLocBasedCall;
    public String startCallLoc;
    public String endCallLoc;

    public Call(String words[]){
        this.name = "CALL";
        this.enable = Boolean.parseBoolean(words[i++]);
        this.timeRange = words[i++];
        this.number = words[i++];
        this.callDuration = Integer.parseInt(words[i++]);
        this.subId = Integer.parseInt(words[i++]);
        this.isMoConf = Boolean.parseBoolean(words[i++]);
        this.moConfNumber = words[i++];
        this.isLocBasedCall = Boolean.parseBoolean(words[i++]);
        String tmp1[] = words[i++].split(":");
        this.startCallLoc = tmp1[1];
        String tmp2[] = words[i++].split(":");
        this.endCallLoc = tmp2[1];
    }
}