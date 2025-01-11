package org.example;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {

    private WorldMap map;
    private List<Animal> animals;

    public Simulation(SimulationSettings settings, WorldMap map) {
        this.map = map;
        this.map.addObserver(new ConsoleMapDisplay());

        Random random = new Random();
        this.animals = new ArrayList<Animal>();
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
        map.nextDay();
    }
}
