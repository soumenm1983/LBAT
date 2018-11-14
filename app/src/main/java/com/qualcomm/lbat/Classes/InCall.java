package com.qualcomm.lbat.Classes;

import java.io.Serializable;

/**
 * Created by shrishn on 9/14/2017.
 */

public class InCall extends ServiceAction implements Serializable {
    public int vtTimer;
    public int holdTimer;
    public int unholdTimer;
    public int muteTimer;
    public int unmuteTimer;
    public int speakerOnTimer;
    public int speakerOffTimer;
    public int moCallTimer;
    public int moConfTimer;
    public int rearCameraOnTimer;
    public int rearCameraOffTimer;
    public int swCallTimer;
    public String swNumber;
    public int swCounts;
    public int swTimeDiff;

    public InCall(String words[]){
        this.name ="IN-CALL";
        this.enable = Boolean.parseBoolean(words[i++]);
        for(int i =2;i<words.length;i++) {
            String tmp[] = words[i].split(":");
            switch (tmp[0]) {
                case "vt":
                    this.vtTimer = Integer.valueOf(tmp[1]);
                    break;
                case "hd":
                    this.holdTimer = Integer.valueOf(tmp[1]);
                    this.unholdTimer = Integer.valueOf(tmp[2]);
                    break;
                case "mu":
                    this.muteTimer = Integer.valueOf(tmp[1]);
                    this.unmuteTimer = Integer.valueOf(tmp[2]);
                    break;
                case "sp":
                    this.speakerOnTimer = Integer.valueOf(tmp[1]);
                    this.speakerOffTimer = Integer.valueOf(tmp[2]);
                    break;
                case "cf":
                    this.moCallTimer = Integer.valueOf(tmp[1]);
                    this.moConfTimer = Integer.valueOf(tmp[2]);
                    break;
                case "cam":
                    this.rearCameraOnTimer = Integer.valueOf(tmp[1]);
                    this.rearCameraOffTimer = Integer.valueOf(tmp[2]);
                    break;
                case "sw":
                    this.swCallTimer = Integer.valueOf(tmp[1]);
                    this.swNumber = tmp[2];
                    this.swCounts = Integer.valueOf(tmp[3]);
                    this.swTimeDiff = Integer.valueOf(tmp[4]);
                    break;
                default:
                    throw new IllegalArgumentException("INVALID IN-CALL ACTION : " + tmp[0]);
            }
        }
    }
}