package jp.simulation.dam;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DamChart extends JFrame {

    // InflowとOutflowのレンダラーをクラス内で保持（チェックボックス制御用）
    private XYLineAndShapeRenderer flowRenderer;

    public DamChart(String title,
                    List<Double> inflowHistory,
                    List<Double> outflowHistory,
                    List<Double> waterLevelHistory) {
        super(title);

        int size = waterLevelHistory.size();

        // ■ 水位用データ（左Y軸）
        XYSeries waterLevelSeries = new XYSeries("Water Level");
        for (int i = 0; i < size; i++) {
            waterLevelSeries.add(i + 1, waterLevelHistory.get(i));
        }
        XYSeriesCollection waterDataset = new XYSeriesCollection(waterLevelSeries);

        // ■ 流入・流出量データ（右Y軸）
        XYSeries inflowSeries = new XYSeries("Inflow");
        XYSeries outflowSeries = new XYSeries("Outflow");
        for (int i = 0; i < size; i++) {
            inflowSeries.add(i + 1, inflowHistory.get(i));
            outflowSeries.add(i + 1, outflowHistory.get(i));
        }
        XYSeriesCollection flowDataset = new XYSeriesCollection();
        flowDataset.addSeries(inflowSeries);   // Series 0: Inflow
        flowDataset.addSeries(outflowSeries);  // Series 1: Outflow

        // ■ メインチャート（左Y軸）作成
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Dam Simulation Result",
                "Day",
                "Water Level",
                waterDataset
        );

        XYPlot plot = chart.getXYPlot();

        // ■ Water Level の線色を赤に設定
        XYLineAndShapeRenderer levelRenderer = new XYLineAndShapeRenderer();
        levelRenderer.setSeriesShapesVisible(0, false); // ← 点を消す
        levelRenderer.setSeriesPaint(0, Color.BLACK);
        levelRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(0, levelRenderer);

        // ■ 右Y軸（Inflow / Outflow）を追加
        NumberAxis rightAxis = new NumberAxis("Inflow / Outflow");
        plot.setRangeAxis(1, rightAxis);
        plot.setDataset(1, flowDataset);
        plot.mapDatasetToRangeAxis(1, 1);

        // flow用のレンダラー（InflowとOutflow両方とも点なし）
        flowRenderer = new XYLineAndShapeRenderer();

        // Inflow（Series 0）：線のみ（必要なら非表示に）
        flowRenderer.setSeriesLinesVisible(0, true);
        flowRenderer.setSeriesShapesVisible(0, false); // ← 点を消す
        flowRenderer.setSeriesPaint(0, Color.RED);
        flowRenderer.setSeriesStroke(0, new BasicStroke(1.2f));

        // Outflow（Series 1）：線のみ
        flowRenderer.setSeriesLinesVisible(1, true);
        flowRenderer.setSeriesShapesVisible(1, false);
        flowRenderer.setSeriesPaint(1, Color.BLUE);
        flowRenderer.setSeriesStroke(1, new BasicStroke(1.2f));

        plot.setRenderer(1, flowRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // ■ チャートパネルの作成
        ChartPanel chartPanel = new ChartPanel(chart);

        // ■ チェックボックス：Inflowの表示切替
        JCheckBox inflowCheckBox = new JCheckBox("Inflow", true);
        inflowCheckBox.addActionListener(e -> {
            boolean selected = inflowCheckBox.isSelected();
            flowRenderer.setSeriesVisible(0, selected); // Series 0 = Inflow
        });

        // ■ UI構成（グラフ＋チェックボックス）
        JPanel controlPanel = new JPanel();
        controlPanel.add(inflowCheckBox);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(chartPanel);
        container.add(controlPanel);

        setContentPane(container);
    }
}
