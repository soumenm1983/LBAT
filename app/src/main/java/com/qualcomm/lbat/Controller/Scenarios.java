package com.qualcomm.lbat.Controller;

/**
 * Created by shrishn on 8/22/2017.
 */

public class Scenarios {
    public  enum SceanrioStatus {
        SERIES,
        PARALLEL,
    }
    private SceanrioStatus sceanrioStatus;
    private String scenarioList;
    private int iteraions;
    private int serviceCount;
    public Object serviceArr[];

    public Scenarios(){
        serviceArr = new Object[5000];
    }

    public void setServiceCount(int serviceCount){
        this.serviceCount = serviceCount;
    }

    public int getServiceCount(){
        return this.serviceCount;
    }

    public void setScenarioList(String scenarioList){
        this.scenarioList = scenarioList;
        this.serviceCount = scenarioList.split("\n").length;
    }

    public String getScenarioList(){
        return this.scenarioList;
    }

    public void setIteraions(int iteraions){
        this.iteraions = iteraions;
    }

    public int getIteraions(){
        return this.iteraions;
    }

    public void setSceanrioStatus(SceanrioStatus sceanrioStatus){
        this.sceanrioStatus = sceanrioStatus;
    }

    public SceanrioStatus getSceanrioStatus(){
        return this.sceanrioStatus;
    }
}
