package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        SimulationSettings settings = new SimulationSettings(20, 20, 50, 5, 5, false, 50, 15, 30, 30, 0, 1, false, 7);
//        WorldMap map = new WorldMap(settings);
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        Simulation simulation = new Simulation(settings, map);
        int i = 0;
        do {
            simulation.run();
            i++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (map.liveAnimalsAmount() != 0);
//        } while (map.liveAnimalsAmount() != 0 && i < 100);
    }

}