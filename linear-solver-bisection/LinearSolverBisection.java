public class LinearSolverBisection {
    public static double f(double x) {
        // 一次方程式 y = -2x + 6
        return -2 * x + 6;
    }

    public static double g(double x, double targetY) {
        return f(x) - targetY;
    }

    public static void main(String[] args) {
        double a = -100;
        double b = 100;
        double epsilon = 1e-6;
        double targetY = 5;

        if (g(a, targetY) * g(b, targetY) >= 0) {
            System.out.println("g(a)とg(b)の符号が同じです。2分法が使えません。");
            return;
        }

        double c = a;
        int iteration = 0;

        while ((b - a) / 2 > epsilon) {
            c = (a + b) / 2;
            double gc = g(c, targetY);

            System.out.printf("Step %d: a = %.6f, b = %.6f, c = %.6f, g(c) = %.6f%n",
                    iteration, a, b, c, gc);

            if (Math.abs(gc) < epsilon) {
                break;
            } else if (g(a, targetY) * gc < 0) {
                b = c;
            } else {
                a = c;
            }
            iteration++;
        }

        System.out.printf("targetY = %.2f のときの近似解 x = %.6f%n", targetY, c);
    }
}
