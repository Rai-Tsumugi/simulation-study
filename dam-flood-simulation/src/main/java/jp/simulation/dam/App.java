package jp.simulation.dam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

public class App {
    public static void main(String[] args) {
        Random random = new Random();
        boolean breakedDam = false;

        // 流入量の候補値（単位: 万m³/s など）
        double[] inflowRates = { 0.0, 0.5, 1.0, 1.5, 2.0, 5.0 };

        // 各流入量に対応する発生確率（合計1.0）
        double[] probabilities = { 0.1, 0.6, 0.2, 0.05, 0.03, 0.02 };

        // 累積確率（random値との比較に使用）
        double[] cumulativeProbabilities = new double[probabilities.length];
        cumulativeProbabilities[0] = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
        }

        // 累積確率の合計が1.0であるかのチェック（精度誤差の検出）
        if (Math.abs(cumulativeProbabilities[cumulativeProbabilities.length - 1] - 1.0) > 1e-6) {
            System.out.println("Error: 確率の合計値が1.0ではありません。");
            return;
        }

        
        double damCapacity = 350.0;          // ダムの最大貯水容量（単位: 任意の体積単位、例: 万m³）
        double waterLevel = 200.0;           // 初期の貯水位（単位: 同上）

        double minOutflowRate = 0.2;         // 最小放流量（単位: 同上）
        double maxOutflowRate = 2.0;         // 最大放流量（単位: 同上）
        double depletionThreshold = 50.0;    // 枯渇と判定する水位（単位: 同上）
        double floodThreshold = 250.0;       // 増水と判定する水位（単位: 同上）
        double curveExponent = 2.0;          // 曲線指数（2.0なら放物線）

        
        int simulationDays = 365*4;            // シミュレーション日数（単位: 日）


        List<Double> waterLevelHistory = new ArrayList<>();
        List<Double> inflowHistory = new ArrayList<>();
        List<Double> outflowHistory = new ArrayList<>();

        // シミュレーションの実行
        for (int day = 0; day < simulationDays; day++) {
            // 流入量の決定
            double inflowRate = 0.0;
            double randomValue = random.nextDouble();
            for (int i = 0; i < cumulativeProbabilities.length; i++) {
                if (randomValue <= cumulativeProbabilities[i]) {
                    inflowRate = inflowRates[i];
                    break;
                }
            }
            // 流出量の決定
            double outflow;
            if (waterLevel <= depletionThreshold) {
                outflow = minOutflowRate;
            } else if (waterLevel >= floodThreshold) {
                outflow = maxOutflowRate;
            } else {
                double ratio = (waterLevel - depletionThreshold) / (floodThreshold - depletionThreshold);
                outflow = minOutflowRate + Math.pow(ratio, curveExponent) * (maxOutflowRate - minOutflowRate);
            }
            
            // 水位の更新
            // 水位がダムの容量を超えた場合、ダムが壊れたとみなす
            waterLevel = breakedDam ? 0.0 : waterLevel + (inflowRate - outflow);
            if (waterLevel < 0.0) {
                waterLevel = 0.0;
                System.out.println("Warning: Water level below zero on day " + (day + 1));
            }
            if (waterLevel > damCapacity) {
                breakedDam = true;
                System.out.println("Warning: Dam overflow on day " + (day + 1));
            }

            // 日ごとの結果を表示（←これがポイント）
            System.out.printf("Day %3d: Inflow = %4.2f, Outflow = %4.2f, Water Level = %6.2f%n",
                day + 1, inflowRate, outflow, waterLevel);

            inflowHistory.add(inflowRate);
            outflowHistory.add(outflow);
            waterLevelHistory.add(waterLevel);
        }
        
        // グラフ表示（最後に）
        javax.swing.SwingUtilities.invokeLater(() -> {
            DamChart chart = new DamChart("Dam Simulation",
                                           inflowHistory,
                                           outflowHistory,
                                           waterLevelHistory);
            chart.setSize(800, 600);
            chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chart.setVisible(true);
        });

    }
}
