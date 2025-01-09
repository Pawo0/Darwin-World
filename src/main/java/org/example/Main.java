package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        SimulationSettings settings = new SimulationSettings(20, 20, 300, 20, 70, false, 30, 15, 30, 30,0,1,false, 7);
        WorldMap map = new WorldMap(settings);
        Simulation simulation = new Simulation(settings, map);
        while (true) {
            simulation.run();
            if (map.liveAnimalsAmount() == 0) {
                break;
            }
        }
    }

}