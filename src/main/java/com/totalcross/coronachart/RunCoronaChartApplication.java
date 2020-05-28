package com.totalcross.coronachart;

import totalcross.TotalCrossApplication;

public class RunCoronaChartApplication {
    public static void main(String [] args) {
        TotalCrossApplication.run(CoronaChartApp.class, 
        "/scr", "960x520",
        "/r", "5443444B5AAEEB90306B00E4");
    }
}
