package com.totalcross.coronachart;

import totalcross.TotalCrossApplication;

public class RunCoronaChartApplication {
    public static void main(String[] args) {
        TotalCrossApplication.run(CoronaChartApp.class,
                // "/scr", "1920x1280",
                // "/scr", "960x640",
                "/scr", "848x480", "/density", "1", "/r", "5443444B5AAEEB90306B00E4");
    }
}
