package com.totalcross.coronachart;

import java.util.List;

import totalcross.sys.Convert;
import totalcross.ui.Control;
import totalcross.ui.Window;
import totalcross.ui.gfx.Color;
import totalcross.ui.gfx.Graphics;
import totalcross.ui.gfx.Rect;

public class CoronaChart<X extends Comparable<X>, Y extends Number> extends Control {

    Series<X, Y>[] series;

    int yMin = 0;
    int yMax = 8 * 1000 * 1000;
    int yStep = 1000 * 1000;

    public CoronaChart(Series<X, Y>... series) {
        this.series = series;
    }

    @Override
    public void onPaint(Graphics g) {
        final int borderGap = 10;
        super.onPaint(g);

        g.foreColor = Color.interpolateA(0x869699, this.backColor, 65);
        Rect r = new Rect(borderGap, borderGap, width - (borderGap * 2), height - (borderGap * 2));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10);

        for (Series<X, Y> series2 : series) {
            g.foreColor = 0xc5cbce;
            List<Data<X, Y>> data = series2.data;
            int nPoints = data.size();
            if (nPoints > 0) {
                int xTextHeight = 52;

                // y axis
                int yTextLen = Math.max(this.fm.stringWidth(Integer.toString(yMax)),
                        this.fm.stringWidth(Integer.toString(yMin)));
                int yGap = 20;
                int yCount = (yMax - yMin) / yStep;
                int yPart = (r.height - xTextHeight - 1) / (yCount + 1);

                for (int i = 0; i < yCount; i++) {
                    String s = Integer.toString(i * yStep);
                    int yPos = r.height - xTextHeight - 1 - (i + 1) * yPart;
                    g.foreColor = Color.interpolateA(0x869699, this.backColor, 65);
                    g.drawLine(r.x, yPos, r.width - yTextLen - yGap, yPos);

                    g.foreColor = 0xc5cbce;
                    g.drawLine(r.width - 1 - yTextLen, yPos, r.width - 1 - yTextLen - yGap, yPos);
                    g.drawText(Convert.toCurrencyString(s, 0), r.width - 1 - yTextLen, yPos - this.fmH / 2);
                }
                g.foreColor = 0xc5cbce;
                g.drawLine(r.width - 1 - yTextLen - yGap, r.y + 1, r.width - 1 - yTextLen - yGap,
                        r.height - xTextHeight - 1);

                // x axis
                g.drawLine(r.x + 1, r.height - xTextHeight - 1, r.width - 1 - yTextLen - yGap,
                        r.height - xTextHeight - 1);
                int widthX = r.width - 1 - yTextLen - yGap;
                int part = widthX / nPoints;
                // int part2 = widthX / (nPoints / 2);
                int xPos = r.x + 1;
                int l2 = (int) (Math.log10(nPoints) / Math.log10(2));
                // part = widthX;
                // try {
                // for (int i = 0; i < l2; i++) {
                // part = BigDecimal.valueOf(part).divide(BigDecimal.valueOf(2), 0,
                // BigDecimal.ROUND_HALF_EVEN)
                // .intValue();
                // }
                // part = BigDecimal.valueOf(widthX).divide(BigDecimal.valueOf(nPoints), 0,
                // BigDecimal.ROUND_HALF_EVEN)
                // .intValue();
                // } catch (ArithmeticException | IllegalArgumentException |
                // InvalidNumberException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }

                int[] xPoints = new int[nPoints];
                int[] yPoints = new int[nPoints];
                for (int i = 0; i < nPoints; i++) {
                    Data<X, Y> series = data.get(i);

                    // x
                    xPos = r.x + 1 + i * part;
                    // if (i % 9 == 0) { 

                    String s = series.x.toString();
                    if (s != null) {
                        g.foreColor = 0xc5cbce;
                        g.drawLine(xPos, r.height - 52 - 1, xPos, r.height - (52 * 3 / 4) - 1);
                        g.drawText(s, xPos - (this.fm.stringWidth(s) / 2), r.height - (52 * 3 / 4) - 1);
                        g.foreColor = Color.interpolateA(0x869699, this.backColor, 65);
                        g.drawLine(xPos, r.y + 1, xPos, r.height - 52 - 1);
                    }
                    // }

                    int yPos = r.height - 52 - 1 - yPart - (int) Math.round(
                            ((double) series.y.intValue() * (double) (r.height - 52 - 1) / (double) (yMax - yMin)));

                    // g.backColor = Color.RED;
                    // g.drawCircle(xPos, yPos, 3);

                    xPoints[i] = xPos;
                    yPoints[i] = yPos;
                }
                g.foreColor = series2.color;
                g.drawDots(r.x + 1, yPoints[yPoints.length - 1], widthX, yPoints[yPoints.length - 1]);
                g.drawPolyline(xPoints, yPoints, nPoints);

                // paint more pixels around to make the line thicker
                for (int i = 0; i < nPoints; i++) {
                    xPoints[i] += 1;
                }
                g.drawPolyline(xPoints, yPoints, nPoints);
                for (int i = 0; i < nPoints; i++) {
                    xPoints[i] -= 2;
                }
                g.drawPolyline(xPoints, yPoints, nPoints);
                for (int i = 0; i < nPoints; i++) {
                    xPoints[i] += 1;
                    yPoints[i] += 1;
                }
                g.drawPolyline(xPoints, yPoints, nPoints);
                for (int i = 0; i < nPoints; i++) {
                    yPoints[i] -= 2;
                }
                g.drawPolyline(xPoints, yPoints, nPoints);
            }
        }
        for (int i = 0; i < series.length; i++) {
            Series<X, Y> series2 = series[i];
            if(series2.data.size() > 0) {
                g.foreColor = Color.WHITE;
                String texto = series2.title;
                g.drawText(texto, r.x + 10, r.y + 6 + (i * this.fmH));
                g.foreColor = series2.color;
                g.drawText(Convert.toCurrencyString(series2.data.get(series2.data.size() - 1).y.toString(), 0),
                        r.x + 15 + this.fm.stringWidth(texto), r.y + 6 + (i * this.fmH));
            }
        }
    }

    public void changeSeries(Series<X, Y>... series) {
        this.series = series;
        Window.needsPaint = true;
    }

    public static class Series<X extends Comparable<X>, Y extends Number> {
        List<Data<X, Y>> data;
        public String title;
        public int color;

        public Series(List<Data<X, Y>> data) {
            this.data = data;
        }
    }

    public static class Data<X extends Comparable<X>, Y extends Number> {
        X x;
        Y y;

        public Data(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}