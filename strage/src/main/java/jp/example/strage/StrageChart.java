package jp.example.strage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class StrageChart extends JFrame {

    // Inflow と Outflow のレンダラーを保持（チェックボックス制御用）
    private final XYLineAndShapeRenderer flowRenderer;

    /**
     * @param title       フレームのタイトル
     * @param history     在庫履歴のリスト
     * @param inHistory   流入履歴のリスト
     * @param outHistory  流出履歴のリスト
     */
    public StrageChart(String title,
                       List<Integer> history,
                       List<Integer> inHistory,
                       List<Integer> outHistory) {
        super(title);

        // --- データシリーズ作成 ---
        XYSeries stockSeries = new XYSeries("Inventory");
        XYSeries inSeries    = new XYSeries("Inflow");
        XYSeries outSeries   = new XYSeries("Outflow");

        int n = history.size();
        for (int i = 0; i < n; i++) {
            stockSeries.add(i, history.get(i));
            if (i < inHistory.size())  inSeries.add(i, inHistory.get(i));
            if (i < outHistory.size()) outSeries.add(i, outHistory.get(i));
        }

        // --- データセット ---
        XYSeriesCollection stockDataset = new XYSeriesCollection(stockSeries);
        XYSeriesCollection flowDataset  = new XYSeriesCollection();
        flowDataset.addSeries(inSeries);
        flowDataset.addSeries(outSeries);

        // --- ベースチャート作成 ---
        JFreeChart chart = ChartFactory.createXYLineChart(
            null,                   // タイトルはフレーム側で設定済み
            "Time",                 // X 軸ラベル
            "Inventory",            // Y 軸ラベル
            stockDataset,           // 在庫データ
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,                   // 凡例表示
            true,                   // ツールチップ
            false                   // URL
        );

        // --- プロット設定 ---
        XYPlot plot = chart.getXYPlot();

        // 在庫系列のレンダラー（デフォルト色）
        XYLineAndShapeRenderer stockRenderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(0, stockRenderer);

        // 流入・流出を別Datasetで追加
        plot.setDataset(1, flowDataset);
        flowRenderer = new XYLineAndShapeRenderer(true, false);
        flowRenderer.setSeriesPaint(0, Color.GREEN);  // Inflow
        flowRenderer.setSeriesPaint(1, Color.RED);    // Outflow
        plot.setRenderer(1, flowRenderer);

        // 描画順序：在庫の下に流入・流出を重ねる
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // X軸の目盛りを整数のみ
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // --- UI 構築 ---
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // チェックボックスで流入／流出の表示制御
        JCheckBox cbIn  = new JCheckBox("Show Inflow",  true);
        JCheckBox cbOut = new JCheckBox("Show Outflow", true);
        cbIn.addItemListener(e -> 
            flowRenderer.setSeriesVisible(0, e.getStateChange() == ItemEvent.SELECTED)
        );
        cbOut.addItemListener(e -> 
            flowRenderer.setSeriesVisible(1, e.getStateChange() == ItemEvent.SELECTED)
        );
        JPanel controlPanel = new JPanel();
        controlPanel.add(cbIn);
        controlPanel.add(cbOut);

        // フレーム配置
        this.setLayout(new BorderLayout());
        this.add(chartPanel,   BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
