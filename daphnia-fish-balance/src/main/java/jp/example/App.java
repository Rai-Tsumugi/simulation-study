package jp.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        double daphnia = 110; // fishDeathRate/fishBirthRateで安定したミジンコの個体数
        // ミジンコ：出生率は50%／捕食率は5%
        double daphniaBirthRate = 0.5;
        double daphniaDeathRate = 0.05;

        double fish = 5; // daphniaBirthRate/daphniaDeathRateで安定した魚の個体数
        // 魚：繁殖率は2%/自然死率は30%
        double fishBirthRate = 0.003;
        double fishDeathRate = 0.3;
        
        // 時間ステップ数
        double dt = 0.01;
        double timeSteps = 100;

        // グラフ用データ系列
        XYSeries daphniaSeries = new XYSeries("Daphnia");
        XYSeries fishSeries = new XYSeries("Fish");

        System.out.printf("Time step %.2f: Daphnia = %.0f, Fish = %.0f%n", 0.0, daphnia, fish);
        daphniaSeries.add(0.0, daphnia);
        fishSeries.add(0.0, fish);

        for (double t = dt; t <= timeSteps; t+=dt) {
            // ミジンコ個体数の更新
            double newDaphnia = daphnia
                    + dt*(daphniaBirthRate * daphnia - daphniaDeathRate * fish * daphnia);
            
            // 魚個体数の更新
            double newFish = fish
                    + dt*(fishBirthRate * daphnia * fish - fishDeathRate * fish);

            daphnia = Math.max(0, newDaphnia);
            fish = Math.max(0, newFish);

            System.out.printf("Time step %.2f: Daphnia = %.0f, Fish = %.0f%n", t, daphnia, fish);
            daphniaSeries.add(t, daphnia);
            fishSeries.add(t, fish);
        }

        // データセットを作成
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(daphniaSeries);
        dataset.addSeries(fishSeries);

        // 折れ線グラフを作成
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Daphnia and Fish Population Over Time",
                "Time Step",
                "Population",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Swingで表示
        JFrame frame = new JFrame("Population Dynamics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
