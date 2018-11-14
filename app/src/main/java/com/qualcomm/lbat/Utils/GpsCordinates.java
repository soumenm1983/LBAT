package com.qualcomm.lbat.Utils;

/**
 * Created by shrishn on 8/28/2017.
 */

public class GpsCordinates {
    private String latitude;
    private String longitude;
    private String altitude;

    public GpsCordinates(){
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getLongitude(){
        return this.longitude;
    }

    public void setAltitude(String altitude){
        this.altitude = altitude;
    }

    public String getAltitude(){
        return this.altitude;
    }
}
