package org.example.model;

import java.util.PriorityQueue;
import java.util.UUID;

public interface WorldMapInterface {
    boolean place(Animal animal) throws IncorrectPositionException;

    void allAnimalsMove();

    void allAnimalsEat();

    void grassGrows();

    void animalCopulate();

    boolean isAnimalAt(Vector2d position);

    boolean isDeadAnimalAt(Vector2d position);

    boolean isGrassAt(Vector2d position);

    void checkForDeadAnimals();

    PriorityQueue<Animal> animalsAt(Vector2d position);

    Boundary getCurrentBounds();

    UUID getId();
}
