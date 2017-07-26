package com.example.junghyen.prototype1start_up;

/**
 * Created by jungh on 2017-02-24.
 */

public class SensorInfoListItemActivity {
    private String check;
    private String time;
    private String incount;
    private String outcount;
    private String left_person;

    public SensorInfoListItemActivity (String check, String time, String incount, String outcount, String left_person){
        this.check = check;
        this.time = time;
        this.incount = incount;
        this.outcount = outcount;
        this.left_person = left_person;
    }

    public String getLeft_person() {
        return left_person;
    }

    public void setLeft_person(String left_person) {
        this.left_person = left_person;
    }

    public String getOutcount() {

        return outcount;
    }

    public void setOutcount(String outcount) {
        this.outcount = outcount;
    }

    public String getIncount() {

        return incount;
    }

    public void setIncount(String incount) {
        this.incount = incount;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCheck() {

        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
