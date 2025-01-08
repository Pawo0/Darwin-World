package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        WorldMap map = new WorldMap();
        Genome genome = new Genome(5);
        System.out.println(genome.getGenome().toString());
        map.addObserver(new ConsoleMapDisplay());
        map.grassGrows();
        map.place(new Animal(genome, new Vector2d(2, 2)));
        map.allAnimalsMove();
        map.allAnimalsMove();
        map.allAnimalsMove();
        map.allAnimalsMove();
        map.allAnimalsMove();
        map.allAnimalsMove();
        map.allAnimalsMove();
    }

}