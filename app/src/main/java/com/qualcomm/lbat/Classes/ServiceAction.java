package com.qualcomm.lbat.Classes;

import com.qualcomm.lbat.Utils.Constants;

import java.io.Serializable;

/**
 * Created by shrishn on 8/24/2017.
 */

public class ServiceAction implements Serializable {
    public String name;
    public boolean enable;
    public String parentFolder = Constants.SDCARD;
    public int i = 1;
}