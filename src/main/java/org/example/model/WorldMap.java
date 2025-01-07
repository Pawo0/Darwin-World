package org.example.model;

import java.util.*;

public class WorldMap implements WorldMapInterface {
    private final UUID id;
    private final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;
    private final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;


    private final Map<Vector2d, Grass> grasses;

    private final Comparator<Animal> animalComparator = new AnimalComparator();

    private final Map<Vector2d, Integer> chanceOfGrassGrowing;

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
        this.energyNeededToCopulate = 50;
        this.chanceOfGrassGrowing = new HashMap<>();
        addGrassGrowingChance();
    }

    public Map<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    private void addGrassGrowingChance() {
        int middleEquator = (upperRight.getX() + lowerLeft.getX()) / 2;
        int equatorTop = middleEquator + middleEquator / 10;
        int equatorBottom = middleEquator - middleEquator / 10;

        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (y > equatorTop) {
                    chanceOfGrassGrowing.put(position, 20);
                } else if (y < equatorBottom) {
                    chanceOfGrassGrowing.put(position, 20);
                } else {
                    chanceOfGrassGrowing.put(position, 80);
                }
            }
        }
    }

    public Map<Vector2d, Integer> getChanceOfGrassGrowing() {
        return chanceOfGrassGrowing;
    }

    @Override
    public boolean place(Animal animal) {
        Vector2d position = animal.getPosition();
        liveAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
        liveAnimals.get(position).add(animal);
        return true;
    }


    @Override
    public void allAnimalsMove() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                liveAnimals.get(animal.getPosition()).remove(animal);
                animal.move();
                this.place(animal);
            }
        }
    }

    @Override
    public void allAnimalsEat() {
//        jesli na polu jest wiecej zwierzat, to je tylko pierwszy - czyli ten co ma najwiecej energii - moze zrobimy od tylu?
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (isGrassAt(animal.getPosition())) {
                    animal.eat();
                    grasses.remove(animal.getPosition());
                }
            }
        }
    }

    @Override
    public void grassGrows() {
        for (Map.Entry<Vector2d, Integer> entry : chanceOfGrassGrowing.entrySet()) {
            if (!isGrassAt(entry.getKey()) && new Random().nextInt(100) < entry.getValue()) {
                grasses.put(entry.getKey(), new Grass(entry.getKey()));
            }
        }
    }

    @Override
    public void animalCopulate() {
        Animal partner;
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getEnergy() > energyNeededToCopulate) {
                    partner = findPartner(animal, animals);
                    if (partner != null) {
                        Animal child = copulate(animal, partner);
                        this.place(child);
                        return;
                    }
                }
            }
        }
    }

    public Animal copulate(Animal animal1, Animal animal2) {
        Vector2d position = animal1.getPosition();
        animal1.substractCopulationEnergy(energyNeededToCopulate);
        animal2.substractCopulationEnergy(energyNeededToCopulate);
        Animal child = new Animal(new Genome(animal1, animal2), position);
        animal1.birthChild(child, energyNeededToCopulate);
        animal2.birthChild(child, energyNeededToCopulate);
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
