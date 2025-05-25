package jp.example.bungee;

import java.util.ArrayList;
import java.util.List;

public class App {
    // --- 定数設定 ---
    private static final double INITIAL_HEIGHT = 60.0;   // スタート位置 (m)
    private static final double CORD_LENGTH    = 10.0;  // 紐の自然長 (m)
    private static final double MASS           = 60.0;    // 質量 (kg)
    private static final double SPRING_K       = 40.0;    // ばね定数 (N/m)
    private static final double G              = 9.8;     // 重力加速度 (m/s^2)
    
    // --- 二次効力モデル用パラメータ ---
    private static final double RHO = 1.225;      // 空気密度 (kg/m^3)
    private static final double CD  = 0.7;        // 抗力係数 頭から飛び込む姿勢[0.5-0.7]
    private static final double A   = 0.3;        // 断面積 (m^2) 頭から飛び込む姿勢[0.1-0.3]

    
    // --- シミュレーション設定 ---
    private static final double dt     = 0.01;    // タイムステップ (s)
    private static final double tMax   = 120.0;    // 最大シミュレーション時間 (s)

    /**
     * 加速度を計算するメソッド
     * @param s 落下した距離(m)
     * @param v 速度(m/s)
     * @return 合成加速度(m/s^2)
     */
    // --- 加速度計算 ---
    private static double acceleration(double s, double v) {
        // ばねの伸び量 (自然長を超えた時のみ)
        double extension =  Math.max(0.0, s - CORD_LENGTH);
        // ばねによる戻し加速度
        double aSpring = - (SPRING_K / MASS) * extension;
        // 空気抵抗による加速度
        double aDrag = - (0.5 * RHO * CD * A * v * Math.abs(v)) / MASS;
        // 重力による加速度
        double aGravity = G;
        // 合成加速度
        return aGravity + aSpring + aDrag;
    }
    public static void main(String[] args) {
        // --- 初期条件 ---
        double s     = 0.0;            // 落下距離(m)。
        double v     = 0.0;            // 速度 (m/s)。下向きを正とする。
        double t     = 0.0;            // 時間 (s)

        // --- 履歴保存用リスト ---
        List<Double> history = new ArrayList<>();
        // 初期値保存
        history.add(INITIAL_HEIGHT);

        // プリントヘッダー
        System.out.printf("%8s %12s %12s %12s%n", "Time(s)", "Position(m)", "Velocity(m/s)", "a/G");
        
        // --- RK4 ループ ---
        while (t < tMax) {
            // 加速度が来て位置を超えたら修了
            double aNorm = acceleration(s, v) / G;
            // 地面に到達したら終了
            double globalPos = INITIAL_HEIGHT - s;

            // 加速度の安全度をチェック
            if (aNorm > 1.5) {
                System.out.printf("Warning: Acceleration exceeds gravity at t=%.2f s (a/G = %.4f). Simulation may be unstable.%n", t, aNorm);
                history.add(globalPos);
                break;
            }
            // 地面に到達したかチェック
            if (globalPos <= 0) {
                System.out.printf("%8.2f %12.4f %12.4f %12.4f  <-- Ground reached%n", t, globalPos, v, aNorm);
                history.add(globalPos);
                break;
            }

            // RK4 のための傾き計算
            // 速度 y' = v, 加速度 v' = g - (k/m)*y
            // k1
            double k1s = v;
            double k1v = acceleration(s, v);

            // k2
            double s2  = s + 0.5 * dt * k1s;
            double v2  = v + 0.5 * dt * k1v;
            double k2s = v2;
            double k2v = acceleration(s2, v2);

            // k3
            double s3  = s + 0.5 * dt * k2s;
            double v3  = v + 0.5 * dt * k2v;
            double k3s = v3;
            double k3v = acceleration(s3, v3);

            // k4
            double s4  = s + dt * k3s;
            double v4  = v + dt * k3v;
            double k4s = v4;
            double k4v = acceleration(s4, v4);

            // RK4 の更新
            s += (dt/6.0)*(k1s + 2*k2s + 2*k3s + k4s);
            v += (dt/6.0)*(k1v + 2*k2v + 2*k3v + k4v);
            t += dt;

            // a/Gの計算
            aNorm = acceleration(s, v) / G;

            // 表示
            System.out.printf("%8.2f %12.4f %12.4f %12.4f%n", t, globalPos, v, aNorm);
            // 履歴保存
            history.add(globalPos);
        }

        // グラフ表示
        javax.swing.SwingUtilities.invokeLater(() -> {
            BungeeChart chart = new BungeeChart("Bungee Simulation", history);
            chart.setSize(800, 600);
            chart.setVisible(true);
        });
    }
}
