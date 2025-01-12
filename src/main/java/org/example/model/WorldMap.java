package org.example.model;

import org.example.MapVisualizer;
import org.example.simulations.SimulationSettings;

import java.util.*;

public class WorldMap implements WorldMapInterface {
    protected final UUID id;
    protected final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;

    public Map<Vector2d, PriorityQueue<Animal>> getDeadAnimals() {
        return deadAnimals;
    }

    protected final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;

    protected int currentDay = 0;


    protected final Map<Vector2d, Grass> grasses;

    protected final Comparator<Animal> animalComparator = new AnimalComparator();


    protected final List<Vector2d> fieldsWithGrassGrowPriority;
    protected final List<Vector2d> fieldsWithoutGrassGrowPriority;


    protected final Boundary boundary;
    protected final int energyNeededToCopulate;
    protected final int energyUsedToCopulate;
    protected final int dailyAmountGrowingGrass;
    protected final SimulationSettings settings;

    protected List<MapChangeListener> observers = new ArrayList<>();

    public WorldMap(SimulationSettings settings) {
        this.id = UUID.randomUUID();
        this.liveAnimals = new HashMap<>();
        this.deadAnimals = new HashMap<>();
        this.grasses = new HashMap<>();
        this.fieldsWithGrassGrowPriority = new ArrayList<>();
        this.fieldsWithoutGrassGrowPriority = new ArrayList<>();

        this.boundary = settings.getBoundary();
        this.energyNeededToCopulate = settings.getEnergyNeededToCopulate();
        this.energyUsedToCopulate = settings.getEnergyUsedToCopulate();
        this.dailyAmountGrowingGrass = settings.getDailyAmountGrowingGrass();
        this.settings = settings;
        initGrassGrowingChance();
        grassGrows(settings.getStartAmountOfGrass());
    }

    public void nextDay() {
        currentDay++;
        allAnimalsAgeUp();
        checkForDeadAnimals();
        allAnimalsMove();
        allAnimalsEat();
        animalCopulate();
        dailyGrassGrow();
        notifyObservers(String.valueOf(liveAnimalsAmount()));
    }

    public Map<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    protected void allAnimalsAgeUp(){
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                animal.incrementAge();
            }
        }
    }

    protected void initGrassGrowingChance() {
//        for every field in the map add chance to grow grass
        for (int x = boundary.lowerLeft().getX(); x <= boundary.upperRight().getX(); x++) {
            for (int y = boundary.lowerLeft().getY(); y <= boundary.upperRight().getY(); y++) {
                addFieldsWithGrassGrowingChance(x, y);
            }
        }
    }

    protected void addFieldsWithGrassGrowingChance(int x, int y) {
//        most often used after eating grass - (zjesz trawe, to znowu jest szansa ze urosnie nastepna)
        Vector2d position = new Vector2d(x, y);
        if (isWithinEquator(position)) {
            fieldsWithGrassGrowPriority.add(position);
        } else {
            fieldsWithoutGrassGrowPriority.add(position);
        }
    }


    protected boolean isWithinEquator(Vector2d position) {
        int y = position.getY();
        int middleEquator = (boundary.upperRight().getX() + boundary.lowerLeft().getX() + 1) / 2;
        int equatorTop = middleEquator + middleEquator / 10;
        int equatorBottom = middleEquator - middleEquator / 10;
        return y >= equatorBottom && y <= equatorTop;
    }


    @Override
    public boolean place(Animal animal) {
        if (animal.getPosition().getX() < boundary.lowerLeft().getX() || animal.getPosition().getX() > boundary.upperRight().getX() || animal.getPosition().getY() < boundary.lowerLeft().getY() || animal.getPosition().getY() > boundary.upperRight().getY()) {
            return false;
        }
        Vector2d position = animal.getPosition();
        liveAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
        liveAnimals.get(position).add(animal);
        return true;
    }

    public void removeLiveAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        liveAnimals.get(position).remove(animal);
        if (liveAnimals.get(position).isEmpty()) {
            liveAnimals.remove(position);
        }
    }

    public void removeDeadAnimal(Animal animal) {
        System.out.println("removing dead animal");
        Vector2d position = animal.getPosition();
        deadAnimals.get(position).remove(animal);
        if (deadAnimals.get(position).isEmpty()) {
            deadAnimals.remove(position);
            System.out.println("dead animals empty");
        }
    }



    @Override
    public void allAnimalsMove() {
        List<Animal> animalsToMove = new ArrayList<>();
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            animalsToMove.addAll(animals);
        }
        for (Animal animal : animalsToMove) {
//            liveAnimals.get(animal.getPosition()).remove(animal);
            removeLiveAnimal(animal);
//            System.out.println("animal moved from: " + animal.getPosition());
            animal.move();
//            System.out.println("animal moved to: " + animal.getPosition());
            this.place(animal);
        }
    }

    @Override
    public void allAnimalsEat() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            if (!animals.isEmpty()) animalEat(animals.peek());
        }
    }

    protected void animalEat(Animal animal) {
        if (isGrassAt(animal.getPosition())) {
            animal.eat();
//            System.out.println("grass eaten at: " + animal.getPosition());
            grasses.remove(animal.getPosition());
            addFieldsWithGrassGrowingChance(animal.getPosition().getX(), animal.getPosition().getY());
        }
    }

    @Override
    public void dailyGrassGrow() {
        grassGrows(dailyAmountGrowingGrass);
    }

    public void grassGrows(int grassAmount) {
        int i = 0;
        while (i < grassAmount) {
            if (fieldsWithGrassGrowPriority.isEmpty()) {
                if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                    break;
                } else {
                    grassGrowOnFields(fieldsWithoutGrassGrowPriority);
                }
            } else if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                grassGrowOnFields(fieldsWithGrassGrowPriority);
            } else {
                double priority = (Math.random() * 100);
                if (priority <= 80) {
                    grassGrowOnFields(fieldsWithGrassGrowPriority);
                } else {
                    grassGrowOnFields(fieldsWithoutGrassGrowPriority);
                }
            }
//            zawsze rosnie bo w fields zawsze jest pole gdzie moze urosnac trawa
            i++;
        }
    }

    protected void grassGrowOnFields(List<Vector2d> fields) {
        int randomIndex = (int) (Math.random() * fields.size());
        Vector2d position = fields.get(randomIndex);
//        System.out.println("grass planted at: " + position);
        grasses.put(position, new Grass(position));
        fields.remove(position);
    }

    @Override
    public void animalCopulate() {
        Animal partner;
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getEnergy() >= energyNeededToCopulate) {
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
        Animal child = new Animal(new Genome(animal1, animal2, this.settings), position, this.settings);
        animal1.birthChild(child, energyUsedToCopulate);
        animal2.birthChild(child, energyUsedToCopulate);
        return child;
    }

    protected Animal findPartner(Animal animal1, PriorityQueue<Animal> animals) {
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
                    animal.animalDeath();
                    animalsToRemove.add(animal);
                }
            }
        }
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.getPosition();
            removeLiveAnimal(animal);
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
