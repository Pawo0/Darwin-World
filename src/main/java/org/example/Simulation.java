package org.example;

import org.example.model.Animal;
import org.example.model.Genome;
import org.example.model.Vector2d;
import org.example.model.WorldMap;

import java.util.List;
import java.util.Random;

public class Simulation {

    private WorldMap map;
    private List<Animal> animals;

    public Simulation(SimulationSettings settings, WorldMap map){
        this.map = map;
        Random random = new Random();
        for (int i = 0; i < 10 /*no of animals*/; i++){
            int x = random.nextInt(settings.width);
            int y = random.nextInt(settings.height);
            animals.add(new Animal(new Genome(69 /*no of genes*/), new Vector2d(x, y)));
        }

        for( Animal animal : animals){
            map.place(animal);
        }
    }

    public void run(){
        map.checkForDeadAnimals();
        map.allAnimalsMove();
        map.allAnimalsEat();
        map.animalCopulate();
        map.grassGrows();
    }
}
