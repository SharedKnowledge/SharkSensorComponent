package net.sharksystem.sensor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

public class SenMLObject {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String bn;
    //bt only gets parsed to json string if the value differs from the default value of 0.0
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    double bt = 0.0;
    String n;
    String u;
    double v;

    public SenMLObject(String bn, double bt, String n, String u, double v) {
        this.bn = bn;
        this.bt = bt;
        this.n = n;
        this.u = u;
        this.v = v;
    }

    public SenMLObject(String n, String u, double v) {
        this.n = n;
        this.u = u;
        this.v = v;
    }

    public SenMLObject() {
    }

    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }
}
