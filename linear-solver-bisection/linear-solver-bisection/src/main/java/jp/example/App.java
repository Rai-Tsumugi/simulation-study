package jp.example;


public class App 
{   
    public static double f(double x) {
        // 一次方程式 y = -2x + 6
        return -2*x + 6;
    }

    // f(x) - targetY を 0 にする x を探すための g(x)
    public static double g(double x, double targetY) {
        return f(x) - targetY;
    }

    public static void main( String[] args )
    {
        // 二分法の初期値
        double a = -100;
        double b = 100;
        double epsilon = 1e-6; // 精度
        double targetY = 5; // 目標値 (y=5のときのxの解を求める)

        if (g(a, targetY) * g(b, targetY) >= 0) {
            System.out.println("g(a)とg(b)の符号が同じです。2分法が使えません。");
            return;
        }

        double c = a;  // 初期化

        int iteration = 0; // 反復回数
        // 二分法
        while ((b - a) / 2 > epsilon) {
            c = (a + b) / 2;
            double gc = g(c, targetY);

            System.out.printf("Step %d: a = %.6f, b = %.6f, c = %.6f, g(c) = %.6f%n",
                    iteration, a, b, c, gc);
                              
            if (gc == 0.0) {
                break; // 正確な解
            } else if (g(a, targetY) * gc < 0) {
                b = c; // 解は [a, c]
            } else {
                a = c; // 解は [c, b]
            }
            iteration++;
        }

        System.out.printf("targetY = %.2f のときの近似解 x = %.6f%n", targetY, c);
    }
}
