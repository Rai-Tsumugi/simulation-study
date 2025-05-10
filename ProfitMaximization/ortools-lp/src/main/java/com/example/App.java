package com.example;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Load the OR-Tools native libraries.
        // This is necessary to use the OR-Tools library.
        Loader.loadNativeLibraries();
        // Create the linear solver with the GLOP backend.
        MPSolver solver = MPSolver.createSolver("GLOP");

        // Create the variables x and y.
        MPVariable x = solver.makeNumVar(0.0, Double.POSITIVE_INFINITY, "x");
        MPVariable y = solver.makeNumVar(0.0, Double.POSITIVE_INFINITY, "y");

        // Create the constraints:
        // 3x + 2y <= 30
        MPConstraint constraint1 = solver.makeConstraint(Double.NEGATIVE_INFINITY, 30.0, "constraint1");
        constraint1.setCoefficient(x, 3);
        constraint1.setCoefficient(y, 2);

        // 10x + 15y <= 150
        MPConstraint constraint2 = solver.makeConstraint(Double.NEGATIVE_INFINITY, 150.0, "constraint2");
        constraint2.setCoefficient(x, 10);
        constraint2.setCoefficient(y, 15);


        // Create the objective function: 4x + 3y
        MPObjective objective = solver.objective();
        objective.setCoefficient(x, 4);
        objective.setCoefficient(y, 3);
        objective.setMaximization();


        // Solve the problem and print the solution.
        if (solver.solve() == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Solution:");
            System.out.println("Objective value = " + objective.value());
            System.out.println("x = " + x.solutionValue());
            System.out.println("y = " + y.solutionValue());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }
}
