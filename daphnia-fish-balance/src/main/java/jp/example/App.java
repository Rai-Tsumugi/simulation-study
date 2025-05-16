package jp.example;


public class App 
{
    public static void main( String[] args )
    {
        int daphnia = 1000;
        int daphniaBirthRate = 10;
        int daphniaDeathRate = 5;
        int fish = 50;
        int fishBirthRate = 2;
        int fishDeathRate = 1;
        int timeSteps = 100;

        for (int t = 0; t < timeSteps; t++) {
            // Calculate the number of daphnia and fish at the next time step
            int newDaphnia = daphnia + (daphniaBirthRate * daphnia) - (daphniaDeathRate * fish/10 * daphnia);
            int newFish = fish + (fishBirthRate * daphnia * fish) - (fishDeathRate * fish);

            // Update the populations
            daphnia = Math.max(0, newDaphnia);
            fish = Math.max(0, newFish);

            // Print the populations at each time step
            System.out.println("Time step " + t + ": Daphnia = " + daphnia + ", Fish = " + fish);
        }
    }
}
