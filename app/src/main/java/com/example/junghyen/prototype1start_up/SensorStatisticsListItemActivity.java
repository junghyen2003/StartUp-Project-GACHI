package com.example.junghyen.prototype1start_up;

/**
 * Created by jungh on 2017-02-24.
 */

public class SensorStatisticsListItemActivity {
    private String date;
    private String incount;
    private String outcount;

    public SensorStatisticsListItemActivity(String date, String incount, String outcount) {
        this.date = date;
        this.incount = incount;
        this.outcount = outcount;
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

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
