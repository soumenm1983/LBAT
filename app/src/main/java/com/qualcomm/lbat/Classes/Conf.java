package com.qualcomm.lbat.Classes;

import android.util.Log;

import com.qualcomm.lbat.Utils.Constants;

import java.io.Serializable;

/**
 * Created by shrishn on 9/27/2017.
 */

public class Conf implements Serializable {
    public String confNumber;
    public int firstTimer;
    public int secondTimer;
    public int subId;
    public String moConfCallTpye;
    private int i=0;

    public Conf(String words[]){
        this.confNumber = words[i++];
        this.firstTimer = Integer.parseInt(words[i++]);
        this.secondTimer = Integer.parseInt(words[i++]);
        this.subId = Integer.parseInt(words[i++]);
        if(words[i].equalsIgnoreCase("I")){
            this.moConfCallTpye = Constants.MO_CONF_IMS;
        }else if(words[i].equalsIgnoreCase("R")){
            this.moConfCallTpye = Constants.MO_CONF_REGULAR;
        }
    }

    public void print(){
        Log.i("CONF-CLASS"," Number : " + this.confNumber + " | " + this.firstTimer + " | " + this.secondTimer + " | " + this.subId + " | " + this.moConfCallTpye);
    }
}
