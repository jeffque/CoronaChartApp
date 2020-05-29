package com.totalcross.coronachart;

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
import totalcross.ui.Label;
import totalcross.ui.MainWindow;
import totalcross.ui.font.Font;
import totalcross.ui.gfx.Color;
import totalcross.util.Date;

public class CoronaChartApp extends MainWindow {

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

        try {
            JSONObject response = new JSONObject(new String(Vm.getFile("request.json")));
            JSONObject data = response.getJSONObject("data");
            JSONArray dates = data.names();
            JSONArray array = data.toJSONArray(dates);

            List<Data<MyDate, Integer>> confirmed = new ArrayList<>();
            List<Data<MyDate, Integer>> recovered = new ArrayList<>();
            List<Data<MyDate, Integer>> deaths = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                String date = dates.getString(i);
                JSONObject item = array.getJSONObject(i);
                confirmed.add(new Data<MyDate, Integer>(new MyDate(Integer.parseInt(Convert.remove(date, "-"))),
                        item.getInt("total_cases")));
                recovered.add(new Data<MyDate, Integer>(new MyDate(Integer.parseInt(Convert.remove(date, "-"))),
                        item.getInt("recovered")));
                deaths.add(new Data<MyDate, Integer>(new MyDate(Integer.parseInt(Convert.remove(date, "-"))),
                        item.getInt("deaths")));
            }
            Collections.sort(confirmed, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            Collections.sort(recovered, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            Collections.sort(deaths, new Comparator<Data<MyDate, Integer>>() {

                @Override
                public int compare(Data<MyDate, Integer> o1, Data<MyDate, Integer> o2) {
                    return o1.x.compareTo(o2.x);
                }
            });

            CoronaChart.Series<MyDate, Integer> confirmedSeries = new CoronaChart.Series<>(confirmed);
            confirmedSeries.title = "Confirmed";
            confirmedSeries.color = 0xf44336;
            CoronaChart.Series<MyDate, Integer> recoveredSeries = new CoronaChart.Series<>(recovered);
            recoveredSeries.title = "Recovered";
            recoveredSeries.color = 0x009688;
            CoronaChart.Series<MyDate, Integer> deathsSeries = new CoronaChart.Series<>(deaths);
            deathsSeries.title = "Deaths";
            deathsSeries.color = Color.WHITE;
            CoronaChart<MyDate, Integer> cc = new CoronaChart<>(confirmedSeries, recoveredSeries, deathsSeries);
            add(cc, LEFT, AFTER + this.fmH, FILL, FILL);
        } catch (/* IOException | */JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class MyDate implements Comparable<MyDate> {
    int date;
    int day;
    int month;
    String s = null;

    public MyDate(int yyyyMMdd) {
        this.date = yyyyMMdd;
        this.day = this.date % 100;
        this.month = yyyyMMdd / 100 % 100;
        if (this.day == 1) {
            s = Date.monthNames[this.month].substring(0, 3);
        } else if (this.day == 10 || this.day == 19) {
            s = Integer.toString(this.day);
        }
    }

    public int compareTo(MyDate o) {
        // TODO Auto-generated method stub
        return this.date - o.date;
    }

    @Override
    public String toString() {
        return s;
    }

}
