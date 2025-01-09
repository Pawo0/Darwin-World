package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        SimulationSettings settings = new SimulationSettings(3, 3, 0, 0, 1, false, 1, 100, 10, 1,0,0,false, 5);
        WorldMap map = new WorldMap(settings);
        Simulation simulation = new Simulation(settings, map);
        for (int i = 0; i < 10; i++){
            simulation.run();
        }

    }

}