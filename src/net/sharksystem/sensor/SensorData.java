package net.sharksystem.sensor;

import java.util.Date;

public class SensorData {




    String bn;
    double temp;
    Unit tempUnit;
    double soil;
    Unit soilUnit;
    double hum;
    Unit humUnit;
    Date dt;

    public SensorData(String bn, double temp, Unit tempUnit, double soil,
                      Unit soilUnit, double hum, Unit humUnit, Date dt) {
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

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
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

}
