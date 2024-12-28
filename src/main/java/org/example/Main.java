package org.example;

import org.example.model.Animal;
import org.example.model.Genome;
import org.example.model.Vector2d;

public class Main {
    public static void main(String[] args) {
        Animal animal1 = new Animal(new Genome(9), new Vector2d(0,0));
        Animal animal2 = new Animal(new Genome(9), new Vector2d(0,0));
        System.out.println("Animal1 " + animal1.getGenotype().getGenome().toString());
        System.out.println("Animal2 " + animal2.getGenotype().getGenome().toString());
        animal2.substractCopulationEnergy(50);
        System.out.println("Animal1 " + animal1.getEnergy());
        System.out.println("Animal2 " + animal2.getEnergy());
        Genome genom = new Genome(animal1,animal2);
        System.out.println(genom.getGenome().toString());
    }

}