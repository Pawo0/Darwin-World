package org.example.interfaces;

import org.example.model.Animal;
import org.example.model.IncorrectPositionException;
import org.example.model.Vector2d;

import java.util.PriorityQueue;
import java.util.UUID;

public interface WorldMapInterface extends AnimalLifecycleInterface, GrassManagementInterface {
    void nextDay();

    boolean place(Animal animal) throws IncorrectPositionException;

    boolean isAnimalAt(Vector2d position);

    boolean isDeadAnimalAt(Vector2d position);

    boolean isGrassAt(Vector2d position);

    PriorityQueue<Animal> getAnimalsAt(Vector2d position);

    UUID getId();
}
