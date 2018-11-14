package com.qualcomm.lbat.Classes;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shrishn on 9/14/2017.
 */

public class MoConfList extends ServiceAction implements Serializable {
    public ArrayList<Conf> confList = new ArrayList<Conf>();

    public MoConfList(String words[]){
        this.name = "MO-CONF";
        this.enable = Boolean.parseBoolean(words[i++]);
        Log.i("MOCONF","LENGTH : "+words.length);
        while(i<words.length){
            String tmp[] = words[i].split(":");
            Conf conf = new Conf(tmp);
            conf.print();
            try {
                boolean status = confList.add(conf);
            }catch (Exception e){
                Log.e("MOCONF", "ERROR : " + e.toString());
                Log.e("MOCONF", "ERROR : " + e.getMessage());
            }
            i++;
        }
    }
}

