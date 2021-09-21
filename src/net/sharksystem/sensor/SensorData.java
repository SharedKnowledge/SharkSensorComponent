package net.sharksystem.sensor;

import java.io.Serializable;

public class SensorData implements Serializable {

    private String bn;
    private double temp;
    private Unit tempUnit;
    private double soil;
    private Unit soilUnit;
    private double hum;
    private Unit humUnit;
    private double dt;

    public SensorData(String bn, double temp, Unit tempUnit, double soil,
                      Unit soilUnit, double hum, Unit humUnit, double dt) {
        this.bn = bn;
        this.temp = temp;
        this.tempUnit = tempUnit;
        this.soil = soil;
        this.soilUnit = soilUnit;
        this.hum = hum;
        this.humUnit = humUnit;
        this.dt = dt;
    }
    public SensorData() {
    }
    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getSoil() {
        return soil;
    }

    public void setSoil(double soil) {
        this.soil = soil;
    }

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public Unit getTempUnit() {
        return tempUnit;
    }
    public void setTempUnit(Unit tempUnit) {
        this.tempUnit = tempUnit;
    }
    public Unit getSoilUnit() {
        return soilUnit;
    }
    public void setSoilUnit(Unit soilUnit) {
        this.soilUnit = soilUnit;
    }
    public Unit getHumUnit() {
        return humUnit;
    }
    public void setHumUnit(Unit humUnit) {
        this.humUnit = humUnit;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass()==SensorData.class){

            SensorData sensorObj = (SensorData)obj;
            if(sensorObj.getBn().equals(this.bn)&&
                    sensorObj.getDt()==this.dt&&
            sensorObj.getTempUnit()==this.tempUnit&&
            sensorObj.getTemp()==this.temp&&
            sensorObj.getHumUnit()==this.humUnit&&
            sensorObj.getHum()==this.hum&&
            sensorObj.getSoilUnit()==this.soilUnit&&
            sensorObj.getSoil()==this.soil){
                return true;
            }
        }
        return false;
    }
}
