package org.example.model;

import org.example.MapVisualizer;

import java.util.*;

public class WorldMap implements WorldMapInterface {
    private final UUID id;
    private final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;
    private final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;


    private final Map<Vector2d, Grass> grasses;

    private final Comparator<Animal> animalComparator = new AnimalComparator();

    private final Map<Vector2d, Integer> chanceOfGrassGrowing;
    private final List<Vector2d> fieldsWithGrassGrowPriority;
    private final List<Vector2d> fieldsWithoutGrassGrowPriority;


    private final Boundary boundary;
    private final int energyNeededToCopulate;
    private final int dailyAmountGrowingGrass;
    private final SimulationSettings settings;

    private List<MapChangeListener> observers = new ArrayList<MapChangeListener>();

    public WorldMap(SimulationSettings settings) {
        this.id = UUID.randomUUID();
        this.liveAnimals = new HashMap<>();
        this.deadAnimals = new HashMap<>();
        this.grasses = new HashMap<>();
        this.fieldsWithGrassGrowPriority = new ArrayList<>();
        this.fieldsWithoutGrassGrowPriority = new ArrayList<>();
        this.chanceOfGrassGrowing = new HashMap<>();

        this.boundary = settings.getBoundary();
        this.energyNeededToCopulate = settings.getEnergyNeededToCopulate();
        this.dailyAmountGrowingGrass = settings.getDailyAmountGrowingGrass();
        this.settings = settings;
        addGrassGrowingChance();
        grassGrows(settings.getStartAmountOfGrass());
    }

    public Map<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    private void addGrassGrowingChance() {
        for (int x = boundary.lowerLeft().getX(); x <= boundary.upperRight().getX(); x++) {
            for (int y = boundary.lowerLeft().getY(); y <= boundary.upperRight().getY(); y++) {
                addFieldsGrassGrowingChance(x, y);
            }
        }
    }

    private void addFieldsGrassGrowingChance(int x, int y) {
        int middleEquator = (boundary.upperRight().getX() + boundary.lowerLeft().getX()) / 2;
        int equatorTop = middleEquator + middleEquator / 10;
        int equatorBottom = middleEquator - middleEquator / 10;
        Vector2d position = new Vector2d(x, y);

        if (y > equatorTop || y < equatorBottom) {
            fieldsWithoutGrassGrowPriority.add(position);
        } else {
            fieldsWithGrassGrowPriority.add(position);
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
        List<Animal> animalsToMove = new ArrayList<>();
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            animalsToMove.addAll(animals);
        }
        for (Animal animal : animalsToMove) {
            liveAnimals.get(animal.getPosition()).remove(animal);
            animal.move();
            this.place(animal);
        }
    }

    @Override
    public void allAnimalsEat() {
//        jesli na polu jest wiecej zwierzat, to je tylko pierwszy - czyli ten co ma najwiecej energii - moze zrobimy od tylu?
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                animalEat(animal);
            }
        }
    }

    private void animalEat(Animal animal){
        if (isGrassAt(animal.getPosition())) {
            animal.eat();
            grasses.remove(animal.getPosition());
            addFieldsGrassGrowingChance(animal.getPosition().getX(), animal.getPosition().getY());
        }
    }

    @Override
    public void dailyGrassGrow() {
        grassGrows(dailyAmountGrowingGrass);
    }

    public void grassGrows(int grassAmount) {
        int i = 0;
//        System.out.println("chuj w dupie chlupie: " + grassAmount);
        while (i < grassAmount) {
            if (fieldsWithGrassGrowPriority.isEmpty()) {
                if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                    break;
                } else {
                    grassGrowOnLowPriorityFields();
                }
            } else if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                grassGrowOnHighPriorityFields();
            } else {
                double priority = (Math.random() * 100);
                if (priority <= 80) {
                    grassGrowOnHighPriorityFields();
                } else {
                    grassGrowOnLowPriorityFields();
                }
            }
//            zawsze rosnie bo w fields zawsze jest pole gdzie moze urosnac trawa
            i++;
        }
    }

    private void grassGrowOnLowPriorityFields() {
        int randomIndex = (int) (Math.random() * fieldsWithoutGrassGrowPriority.size());
        Vector2d position = fieldsWithoutGrassGrowPriority.get(randomIndex);
        grasses.put(position, new Grass(position));
        fieldsWithoutGrassGrowPriority.remove(position);
    }

    private void grassGrowOnHighPriorityFields() {
        int randomIndex = (int) (Math.random() * fieldsWithGrassGrowPriority.size());
        Vector2d position = fieldsWithGrassGrowPriority.get(randomIndex);
        grasses.put(position, new Grass(position));
        fieldsWithGrassGrowPriority.remove(position);
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
                        break;
                    }
                }
            }
        }
    }

    public Animal copulate(Animal animal1, Animal animal2) {
        Vector2d position = animal1.getPosition();
        animal1.subtractCopulationEnergy(energyNeededToCopulate);
        animal2.subtractCopulationEnergy(energyNeededToCopulate);
        Animal child = new Animal(new Genome(animal1, animal2, this.settings), position, this.settings);
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

    public Grass getGrassAt(Vector2d position) {
        return grasses.get(position);
    }


    @Override
    public void checkForDeadAnimals() {
        List<Animal> animalsToRemove = new ArrayList<>();
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getEnergy() < 0) {
                    animalsToRemove.add(animal);
                }
            }
        }
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.getPosition();
            liveAnimals.get(position).remove(animal);
            deadAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
            deadAnimals.get(position).add(animal);
        }
    }


    @Override
    public UUID getId() {
        return this.id;
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }
    public String toString() {
        return new MapVisualizer(this).draw(boundary.lowerLeft(), boundary.upperRight());
    }

    public int liveAnimalsAmount() {
        int amount = 0;
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            amount += animals.size();
        }
        return amount;
    }
}
