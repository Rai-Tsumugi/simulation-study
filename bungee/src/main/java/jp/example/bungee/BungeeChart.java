package jp.example.bungee;

import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class BungeeChart extends JFrame {
    // シミュレーションで使ったタイムステップ（App.java と合わせる）
    private static final double DT = 0.01;

    /**
     * @param title フレームのタイトル
     * @param positionHistory 位置履歴のリスト
     */
    public BungeeChart(String title, List<Double> positionHistory) {
        super(title);
        // データ系列の作成
        XYSeries series = new XYSeries("Position (m)");
        for (int i=0; i < positionHistory.size(); i++) {
            double t = i * DT; // 時間を計算
            double y = positionHistory.get(i);
            series.add(t, y);
        }

        // データセットに登録
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // チャートの作成
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Bungee Jump Simulation",
            "Time (s)",
            "Position (m)",
            dataset,
            PlotOrientation.VERTICAL,
            true, // 凡例
            true, // ツールチップ
            false // URL
        );

        // チャートパネルの作成
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        setContentPane(chartPanel);

        // フレームの基本設定
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
