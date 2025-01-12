package org.example.simulations;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation implements Runnable{

    private WorldMap map;
    private List<Animal> animals;
    private SimulationSettings settings;

    public Simulation(SimulationSettings settings, WorldMap map) {
        this.map = map;
//        this.map.addObserver(new ConsoleMapDisplay());
        this.settings = settings;

        Random random = new Random();
        this.animals = new ArrayList<>();
        for (int i = 0; i < settings.getStartAmountOfAnimals(); i++) {
            int x = random.nextInt(settings.getMapWidth());
            int y = random.nextInt(settings.getMapHeight());
            animals.add(new Animal(new Genome(settings), new Vector2d(x, y), settings));
        }

        for (Animal animal : animals) {
            map.place(animal);
        }
    }

    public void run() {
        int i = 0;
        do {
            map.nextDay();
            i++;
            try {
                Thread.sleep(settings.getRefreshTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Day: " + i);
        } while (map.liveAnimalsAmount() != 0);
    }

}
