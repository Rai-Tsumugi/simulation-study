import java.util.Random;

public class MonteCarloPi {

    public static void main(String[] args) {
        int totalPoints = 10_000_000;  // 実験点の数
        int pointsInsideCircle = 0;

        Random random = new Random();

        for (int i = 0; i < totalPoints; i++) {
            double x = random.nextDouble(); // 0 <= x < 1
            double y = random.nextDouble(); // 0 <= y < 1

            if (x * x + y * y <= 1.0) {
                pointsInsideCircle++;
            }
        }

        double estimatedPi = 4.0 * pointsInsideCircle / totalPoints;
        System.out.printf("モンテカルロ法によるπの近似値: %.10f%n", estimatedPi);
    }
}
