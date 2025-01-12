package org.example.simulations;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine {
    private final List<Simulation> simulations;
    private final List<Thread> threads;

    public SimulationEngine(List<Simulation> simulations) {
        this.simulations = simulations;
        this.threads = new ArrayList<>();
    }

    public void runAsync() throws InterruptedException {
        for (Simulation simulation : simulations) {
            Thread thread = new Thread(simulation);
            thread.start();
            threads.add(thread);
        }
//        awaitSimulationsEnd();
    }


    private void awaitSimulationsEnd() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

}
