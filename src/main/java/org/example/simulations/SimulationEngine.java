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
            threads.add(thread);
            thread.start();
        }
    }

//    to bedzie lepsze do wukorzystania po kliknieciu guzika START w GUI (o ile dziala)
    public void start(){
        for (Simulation simulation : simulations) {
            if (simulation.isRunning()){
                simulation.resume();
            } else{
                simulation.run();
            }
        }
    }

    public void pause(){
        for (Simulation simulation : simulations) {
            simulation.pause();
        }
    }

    public void resume(){
        for (Simulation simulation : simulations) {
            simulation.resume();
        }
    }

    public void stop(){
        for (Simulation simulation : simulations) {
            simulation.stop();
        }
    }


}
