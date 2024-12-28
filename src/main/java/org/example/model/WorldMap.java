package org.example.model;

import java.util.*;

public class WorldMap implements WorldMapInterface {
    private final UUID id;
    private final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;
    private final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;
    private final Map<Vector2d, Grass> grasses;

    private final Comparator<Animal> animalComparator = new AnimalComparator();

    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    private final int energyNeededToCopulate;

    public WorldMap() {
        this.id = UUID.randomUUID();
        this.liveAnimals = new HashMap<>();
        this.deadAnimals = new HashMap<>();
        this.grasses = new HashMap<>();

//        bedzie pobierane z ustawien
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(9, 9);
        this.energyNeededToCopulate = 100;
    }


    @Override
    public boolean place(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        liveAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
        return true;
    }

    @Override
    public void allAnimalsMove() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                animal.move();
            }
        }
    }

    @Override
    public void allAnimalsEat() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                animal.eat();
            }
        }
    }

    @Override
    public void grassGrows() {
//        todo: implementacja rosniecia trawy
    }

    @Override
    public void animalCopulate() {
        Animal partner;
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getEnergy() > energyNeededToCopulate) {
                    partner = findPartner(animal, animals);
                    if (partner != null) {
                        animals.add(copulate(animal, partner));
                    }
                }
            }
        }
    }

    public Animal copulate(Animal animal1, Animal animal2) {
        Vector2d position = animal1.getPosition();
//        todo: stworz genom z rodzicow
        Animal child = new Animal(List.of(0), position);
        liveAnimals.get(position).add(child);
        return child;
    }

    public Animal findPartner(Animal animal1, PriorityQueue<Animal> animals) {
        for (Animal animal : animals) {
            if (!animal.equals(animal1) && animal.getEnergy() > energyNeededToCopulate) {
                return animal;
            }
        }
        return null;
    }

    @Override
    public boolean isAnimalAt(Vector2d position) {
        return liveAnimals.containsKey(position);
    }

    @Override
    public boolean isDeadAnimalAt(Vector2d position) {
        return deadAnimals.containsKey(position);
    }

    @Override
    public PriorityQueue<Animal> animalsAt(Vector2d position) {
        return liveAnimals.get(position);
    }

    @Override
    public boolean isGrassAt(Vector2d position) {
        return grasses.containsKey(position);
    }

    @Override
    public void checkForDeadAnimals() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getEnergy() < 0) {
                    Vector2d position = animal.getPosition();
                    animals.remove(animal);
//                    jeśli nie ma na polu zadnego martwego tworzymy kolejke
                    deadAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
                    deadAnimals.get(position).add(animal);
                }
            }
        }
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(lowerLeft, upperRight);
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
