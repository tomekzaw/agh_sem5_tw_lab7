import base.AbstractDemo;

import java.io.FileNotFoundException;
import java.util.Map;

public class Main {
    private final static int numberOfPhilosophers = 5; // number of philosophers
    private final static int numberOfMeals = 1000; // number of meals
    private final static boolean commonPoolOfMeals = false; // common or individual pool of meals

    public static void main(final String[] args) throws FileNotFoundException {
        Map<String,AbstractDemo> demos = Map.of(
            // "naive", new naive.Demo(numberOfPhilosophers, numberOfMeals)
            "simultaneous", new simultaneous.Demo(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals),
            "asymmetric", new asymmetric.Demo(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals),
            "waiter", new waiter.Demo(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals)
        );

        for (Map.Entry<String,AbstractDemo> entry : demos.entrySet()) {
            String name = entry.getKey();
            AbstractDemo demo = entry.getValue();
            System.out.println(name);
            demo.run("resources/results/java_" + name + ".log");
        }
    }
}
