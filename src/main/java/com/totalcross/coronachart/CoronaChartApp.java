package com.totalcross.coronachart;

import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.totalcross.coronachart.CoronaChart.Data;

import totalcross.json.JSONArray;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;
import totalcross.sys.Convert;
import totalcross.sys.Settings;
import totalcross.sys.Vm;
import totalcross.ui.Button;
import totalcross.ui.Label;
import totalcross.ui.MainWindow;
import totalcross.ui.event.UpdateListener;
import totalcross.ui.font.Font;
import totalcross.ui.gfx.Color;
import totalcross.util.Date;
import totalcross.util.InvalidDateException;

public class CoronaChartApp extends MainWindow {

    final static int ANIMATION_TIME = 5000;
    int currentAnimationTime = 0;
    int nextStopTime = 0;
    int index = 0;
    JSONArray dates;
    JSONArray array;
    List<Data<MyDate, Integer>> confirmed = new ArrayList<>();
    List<Data<MyDate, Integer>> recovered = new ArrayList<>();
    List<Data<MyDate, Integer>> deaths = new ArrayList<>();
    String date;
    JSONObject item;
    CoronaChart.Series<MyDate, Integer> confirmedSeries;
    CoronaChart.Series<MyDate, Integer> recoveredSeries;
    CoronaChart.Series<MyDate, Integer> deathsSeries;
    CoronaChart<MyDate, Integer> cc;
    List<Data<MyDate, Integer>> confirmedList = new ArrayList<>();
    List<Data<MyDate, Integer>> recoveredList = new ArrayList<>();
    List<Data<MyDate, Integer>> deathsList = new ArrayList<>();
    public static Date firstDay;

    UpdateListener updateListener = new UpdateListener() {

        @Override
        public void updateListenerTriggered(int elapsedMilliseconds) {
            if (currentAnimationTime != 0) {
                currentAnimationTime += elapsedMilliseconds;
            } else {
                currentAnimationTime += 1;
            }
            if (currentAnimationTime >= nextStopTime && index != confirmedList.size()) {
                switch(MyDate.mode) {
                    case MyDate.MONTH:
                        if(confirmedList.get(index).x.toDate().equals(firstDay) || confirmedList.get(index).x.day == 1 || index == confirmedList.size() - 1) {
                            changeSeries(index);
                        } 
                    break;
                    case MyDate.WEEK:
                        if (confirmedList.get(index).x.exactlyXWeeksSinceDate(MyDate.firstDay)){
                            changeSeries(index);
                        } 
                    break;
                    case MyDate.DAY:
                        changeSeries(index);
                    break;
                }
                if(index != confirmedList.size() -1 ) {
                    index++;
                } else {
                    MainWindow.getMainWindow().removeUpdateListener(this);
                }
                nextStopTime = (int) (ANIMATION_TIME * easeInQuad(1.0 * index / confirmedList.size()));
            }
        }
    };

    public CoronaChartApp() {
        setUIStyle(Settings.MATERIAL_UI);
    }

    @Override
    public void initUI() {
        this.backColor = 0x131722;
        Label lblTitle = new Label("Coronavirus (COVID-19) charts and stats", CENTER);
        lblTitle.setForeColor(Color.WHITE);
        lblTitle.setFont(Font.getFont(true, 36));
        add(lblTitle, LEFT, TOP + this.fmH, FILL, PREFERRED);
        Button btnStartStop = new Button("Start");
        btnStartStop.addPressListener(e -> {
            if(btnStartStop.getText().equals("Start")) {
                index = 0;
                currentAnimationTime = 0;
                nextStopTime = 0;
                MainWindow.getMainWindow().addUpdateListener(updateListener);
                btnStartStop.setText("Stop");
            } else {
                MainWindow.getMainWindow().removeUpdateListener(updateListener);
                btnStartStop.setText("Start");
            }
        });

        Button btnDay = new Button("Day");
        btnDay.addPressListener(e -> {
            changeDateDisplayMode(MyDate.DAY);
            restartAnimation();
        });
        Button btnWeek = new Button("Week");
        btnWeek.addPressListener(e -> {
            changeDateDisplayMode(MyDate.WEEK);
            restartAnimation();
        });
        Button btnMonth = new Button("Month");
        btnMonth.addPressListener(e -> {
            changeDateDisplayMode(MyDate.MONTH);
            restartAnimation();
        });

        add(btnStartStop, LEFT + this.fmH, AFTER + this.fmH);
        add(btnMonth, RIGHT - this.fmH, SAME);
        add(btnWeek, BEFORE - this.fmH, SAME);
        add(btnDay, BEFORE - this.fmH, SAME);

        try {
            JSONObject response = new JSONObject(new String(Vm.getFile("request.json")));
            JSONObject data = response.getJSONObject("data");
            dates = data.names();
            array = data.toJSONArray(dates);
            for (int i = 0; i < array.length(); i++) {
                date = dates.getString(i);
                item = array.getJSONObject(i);
                MyDate myDate = new MyDate(Integer.parseInt(Convert.remove(date, "-")));
                confirmedList.add(new Data<MyDate, Integer>(myDate,
                        item.getInt("total_cases")));
                recoveredList.add(new Data<MyDate, Integer>(myDate,
                        item.getInt("recovered")));
                deathsList.add(new Data<MyDate, Integer>(myDate,
                        item.getInt("deaths")));
            }
            

            Collections.sort(confirmedList, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            Collections.sort(recoveredList, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            Collections.sort(deathsList, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            MyDate.firstDay = confirmedList.get(0).x.toDate();
            confirmed.add(confirmedList.get(index));
            recovered.add(recoveredList.get(index));
            deaths.add(deathsList.get(index));
            confirmedSeries = new CoronaChart.Series<>(confirmed);
            confirmedSeries.title = "Confirmed";
            confirmedSeries.color = 0xf44336;
            recoveredSeries = new CoronaChart.Series<>(recovered);
            recoveredSeries.title = "Recovered";
            recoveredSeries.color = 0x009688;
            deathsSeries = new CoronaChart.Series<>(deaths);
            deathsSeries.title = "Deaths";
            deathsSeries.color = Color.WHITE;
            cc = new CoronaChart<>(confirmedSeries, recoveredSeries, deathsSeries);
            add(cc, LEFT, AFTER + this.fmH, FILL, FILL);
        } catch (/* IOException | */JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void restartAnimation() {
        MainWindow.getMainWindow().removeUpdateListener(updateListener);
        confirmed.clear();
        recovered.clear();
        deaths.clear();
        index = 0;
        currentAnimationTime = 0;
        nextStopTime = 0;
        MainWindow.getMainWindow().addUpdateListener(updateListener);
    }

    double easeInQuad(double x) {
        return x * x * x * x;
    }

    public void changeDateDisplayMode(int mode) {
        for(int i = 0; i < this.confirmedList.size(); i++) {
            this.confirmedList.get(i).x.changeMode(mode);
        }
    }

    public void changeSeries(int index) {
        confirmed.add(confirmedList.get(index));
        recovered.add(recoveredList.get(index));
        deaths.add(deathsList.get(index));

        confirmedSeries = new CoronaChart.Series<>(confirmed);
        confirmedSeries.title = "Confirmed";
        confirmedSeries.color = 0xf44336;
        recoveredSeries = new CoronaChart.Series<>(recovered);
        recoveredSeries.title = "Recovered";
        recoveredSeries.color = 0x009688;
        deathsSeries = new CoronaChart.Series<>(deaths);
        deathsSeries.title = "Deaths";
        deathsSeries.color = Color.WHITE;
        cc.changeSeries(confirmedSeries, recoveredSeries, deathsSeries);
    }
}

class MyDate implements Comparable<MyDate> {
    int date;
    int day;
    int month;
    String s = null;
    public static int mode;
    public static final int DAY = 0;
    public static final int MONTH = 1;
    public static final int WEEK = 2;
    public static Date firstDay;

    public MyDate(int yyyyMMdd) {
        this.date = yyyyMMdd;
        this.day = this.date % 100;
        this.month = yyyyMMdd / 100 % 100;
        MyDate.mode = DAY;
        if (this.day == 1) {
            s = Date.monthNames[this.month].substring(0, 3);
        } else if (this.day == 10 || this.day == 19) {
            s = Integer.toString(this.day);
        }
    }

    public void changeMode(int mode) {
        MyDate.mode = mode;
        switch(mode) {
            case DAY:
                if (this.day == 1) {
                    s = Date.monthNames[this.month].substring(0, 3);
                } else if (this.day == 10 || this.day == 19) {
                    s = Integer.toString(this.day);
                } else {
                    s = null;
                }
            break;
            case MONTH:
                if(this.toDate().equals(firstDay) || this.day == 1) {
                    s = Date.monthNames[this.month].substring(0, 3);
                } else {
                    s = null;
                }
            break;
            case WEEK:
                if(exactlyXWeeksSinceDate(MyDate.firstDay)){
                    s = weeksSinceDate(MyDate.firstDay);
                } else {
                    s = null;
                }
            break;
        } 
    }

    public String weeksSinceDate(Date date) {
        return date != null ? Integer.toString(date.subtract(this.toDate())/7) : null;
    }

    public boolean exactlyXWeeksSinceDate(Date date) {
        return date != null ? date.subtract(this.toDate())%7 == 0 : false;
    }

    public int compareTo(MyDate o) {
        // TODO Auto-generated method stub
        return this.date - o.date;
    }

    public Date toDate() {
        int year = this.date / 10000;
        Date myDate = null;
        try {
            myDate = new Date(year + "-" + this.month + "-" + this.day, Settings.DATE_YMD);
        } catch (InvalidDateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myDate;
    }

    @Override
    public String toString() {
        return s;
    }
    

}

