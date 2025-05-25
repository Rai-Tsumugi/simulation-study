package jp.example.strage;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 * 在庫シミュレーションのエントリポイント
 */
public class App {

    // ── シミュレーション定数 ──
    private static final int   INITIAL_INVENTORY    = 100;  // 初期在庫
    private static final int   MAX_INVENTORY        = 200;  // 最大在庫
    private static final int   ORDER_FREQUENCY_DAYS = 5;    // 発注頻度（日）
    private static final int   SIMULATION_DAYS      = 365;  // シミュレーション日数

    // ── 販売量モデル（正規分布） ──
    private static final double MEAN_SALES = 25.0;  // 平均販売量
    private static final double SD_SALES   = 10.0;  // 標準偏差

    public static void main(String[] args) {
        Random random = new Random();

        // 履歴データ
        List<Integer> inventoryHistory = new ArrayList<>(); // 在庫履歴
        List<Integer> orderHistory     = new ArrayList<>(); // 発注履歴
        List<Integer> salesHistory     = new ArrayList<>(); // 販売履歴

        // 統計カウンタ
        int inventory       = INITIAL_INVENTORY; // 初期在庫
        int stockoutCount   = 0; // 在庫不足回数
        int stockfullCount  = 0; // 在庫過剰回数
        int stockoutLoss    = 0; // 在庫不足による機会損失量

        // シミュレーションスタート（初期値記録）
        inventoryHistory.add(inventory);
        orderHistory.add(0);
        salesHistory.add(0);

        // 日次ループ
        for (int day = 1; day <= SIMULATION_DAYS; day++) {
            // １．発注量を決定
            int orderQuantity = computeOrderQuantity(day, inventory);

            // ２．販売量を決定
            int salesVolume = computeSalesVolume(random);

            // ３．在庫更新と不足・過剰チェック
            int prevInventory = inventory;
            inventory = inventory + orderQuantity - salesVolume;

            if (inventory < 0) {
                stockoutCount++;
                int lost = salesVolume - prevInventory;
                stockoutLoss += lost;
                inventory = 0;
            } else if (inventory > MAX_INVENTORY) {
                stockfullCount++;
                inventory = MAX_INVENTORY;
            }

            // ４．履歴に保存
            inventoryHistory.add(inventory);
            orderHistory.add(orderQuantity);
            salesHistory.add(salesVolume);

            // → （デバッグ用）毎日の出力
            System.out.printf(
                "日数:%3d, 発注:%3d, 販売:%3d, 在庫:%4d%n",
                day, orderQuantity, salesVolume, inventory
            );
        }

        // 結果集計＆表示
        printSummary(
            stockoutCount,
            stockoutLoss,
            stockfullCount,
            inventoryHistory
        );

        // グラフ表示
        javax.swing.SwingUtilities.invokeLater(() -> {
            StrageChart chart = new StrageChart(
                "Inventory Simulation",
                inventoryHistory,
                orderHistory,
                salesHistory
            );
            chart.setSize(800, 600);
            chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chart.setVisible(true);
        });
    }

    /** 発注量を決定するロジック */
    private static int computeOrderQuantity(int day, int inventory) {
        if (day % ORDER_FREQUENCY_DAYS == 0) {
            // 目標在庫レベルは (平均+標準偏差)×頻度 の簡易モデル
            int target = (int) Math.round((MEAN_SALES + SD_SALES) * ORDER_FREQUENCY_DAYS);
            return Math.max(0, target - inventory);
        }
        return 0;
    }

    /** 正規分布に従って販売量を生成 */
    private static int computeSalesVolume(Random rnd) {
        double gaussian = rnd.nextGaussian(); // N(0,1)
        int raw = (int) Math.round(MEAN_SALES + SD_SALES * gaussian);
        return Math.max(0, raw);
    }

    /** シミュレーション結果をまとめて出力 */
    private static void printSummary(
            int stockoutCount,
            int stockoutLoss,
            int stockfullCount,
            List<Integer> inventoryHistory
    ) {
        int days = SIMULATION_DAYS;
        double averageInventory = inventoryHistory.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        int maxInventory = inventoryHistory.stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);

        System.out.println("=== シミュレーション結果 ===");
        System.out.printf("在庫不足回数: %d%n", stockoutCount);
        System.out.printf("在庫不足量(機会損失): %d%n", stockoutLoss);
        System.out.printf("在庫不足率: %.2f%%%n", (100.0 * stockoutCount) / days);
        System.out.printf("在庫過剰回数: %d%n", stockfullCount);
        System.out.printf("最大在庫量: %d%n", maxInventory);
        System.out.printf("平均在庫量: %.2f%n", averageInventory);
    }
}
