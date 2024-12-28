package org.example.model;

import java.util.Comparator;
import java.util.Random;

public class AnimalComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal a1, Animal a2) {
//        jeśli oba zwierzęta są martwe, to porównujemy po dniu śmierci
//        teoretycznie moznabybylo zrobic drugi comparator, ale po co
        if (a1.getDeathDate() == -1 && a2.getDeathDate() == -1) {
            return Integer.compare(a2.getDeathDate(), a1.getDeathDate());
        }
//       dla zywych porownujemy po energii, wieku, liczbie dzieci, a jak wszystko jest takie samo to losujemy
        else if (a1.getEnergy() != a2.getEnergy()) {
            return Integer.compare(a2.getEnergy(), a1.getEnergy());
        } else if (a1.getAge() != a2.getAge()) {
            return Integer.compare(a2.getAge(), a1.getAge());
        } else if (a1.getChildrenCounter() != a2.getChildrenCounter()) {
            return Integer.compare(a2.getChildrenCounter(), a1.getChildrenCounter());
        } else {
            return new Random().nextInt(2) * 2 - 1;
        }
    }
}
