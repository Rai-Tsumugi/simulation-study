package jp.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        // ─── サンプル数を対数間隔で自動生成 ────────────────
        // 10^minExp から 10^maxExp を、eachDecSteps 分割
        int minExp = 3;      // 10^3 = 1_000
        int maxExp = 8;      // 10^8 = 100_000_000
        int eachDecSteps = 10; // 1デケードを n 分割（10の^(1/n)）
        
        List<Long> sampleCounts = new ArrayList<>();
        for (int exp = minExp; exp < maxExp; exp++) {
            for (int i = 0; i < eachDecSteps; i++) {
                double factor = Math.pow(10, (double)i / eachDecSteps);
                long n = (long)(Math.pow(10, exp) * factor);
                sampleCounts.add(n);
            }
        }
        // 最後に maxExp の値を明示的に追加
        sampleCounts.add((long)Math.pow(10, maxExp));

        // ─── モンテカルロのメインループ ─────────────────
        XYSeries series = new XYSeries("Monte Carlo π 推定");
        Random rand = new Random();
        long inside = 0;
        int idx = 0;
        long nextThreshold = sampleCounts.get(idx);
        long maxSample = sampleCounts.get(sampleCounts.size() - 1);

        for (long i = 1; i <= maxSample; i++) {
            double x = rand.nextDouble(), y = rand.nextDouble();
            if (x*x + y*y <= 1.0) inside++;

            // 閾値に達したらその時点の推定値をシリーズに追加
            if (i == nextThreshold) {
                double piEstimate = 4.0 * inside / i;
                series.add(i, piEstimate);
                System.out.printf("Samples: %,d → π ≒ %.6f%n", i, piEstimate);

                idx++;
                if (idx < sampleCounts.size()) {
                    nextThreshold = sampleCounts.get(idx);
                } else {
                    break;  // すべて記録し終わったらループ終了
                }
            }
        }

        // ─── データセット & グラフ作成 ───────────────────────
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Monte Carlo による π の推定",
            "サンプル数",
            "推定値",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );

        // ─── 日本語フォント設定 ────────────────────────────
        Font font = new Font("MS Gothic", Font.PLAIN, 12);
        chart.getTitle().setFont(font);
        chart.getLegend().setItemFont(font);
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        plot.getRangeAxis().setTickLabelFont(font);

        // ─── X軸：対数目盛 ────────────────────────────────
        LogarithmicAxis logAxis = new LogarithmicAxis("サンプル数（対数目盛）");
        logAxis.setLabelFont(font);
        logAxis.setTickLabelFont(font);
        plot.setDomainAxis(logAxis);

        // ─── Y軸：π付近を拡大 ─────────────────────────────
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(3.10, 3.18);

        // ─── 基準線（πの値）を追加 ────────────────────────
        Marker piMarker = new ValueMarker(Math.PI); // πの値
        piMarker.setPaint(Color.RED);
        piMarker.setStroke(new java.awt.BasicStroke(1.5f));
        piMarker.setLabel("π");
        piMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        piMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        plot.addRangeMarker(piMarker);

        // ─── Swing 表示 ───────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("π 推定グラフ");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}