package com.qualcomm.lbat.Classes;

import java.io.Serializable;

/**
 * Created by shrishn on 9/14/2017.
 */

public class GPS extends ServiceAction implements Serializable {

    public int gpsOnTimer;
    public String gpsTimeRange;


    public GPS(String words[]){
        this.name = "GPS";
        this.enable = Boolean.parseBoolean(words[i++]);
        this.gpsTimeRange = words[i++];
        this.gpsOnTimer = Integer.parseInt(words[i++]);
    }
}
