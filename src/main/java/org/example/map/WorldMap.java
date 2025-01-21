package org.example.map;

import org.example.consoleview.MapVisualizer;
import org.example.genomes.Genome;
import org.example.genomes.GenomeSwap;
import org.example.interfaces.MapChangeListener;
import org.example.interfaces.MapObserver;
import org.example.interfaces.WorldMapInterface;
import org.example.map.objects.Animal;
import org.example.map.objects.Grass;
import org.example.map.objects.MapObjectType;
import org.example.model.*;
import org.example.simulations.SimulationSettings;

import java.util.*;

public class WorldMap implements WorldMapInterface, MapObserver {
    protected final UUID id;
    protected final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;
    protected final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;
    protected final Map<Vector2d, Grass> grasses;

    protected final Comparator<Animal> animalComparator = new AnimalComparator();

    protected final List<Vector2d> fieldsWithGrassGrowPriority;
    protected final List<Vector2d> fieldsWithoutGrassGrowPriority;


    protected final SimulationSettings settings;
    protected final Boundary boundary;
    protected final int energyNeededToCopulate;
    protected final int energyUsedToCopulate;
    protected final int dailyAmountGrowingGrass;

    protected int currentDay = 0;

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

    @Override
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

    // grass init
    protected void initGrassGrowingChance() {
//        for every field in the map add chance to grow grass
        for (int x = boundary.lowerLeft().getX(); x <= boundary.upperRight().getX(); x++) {
            for (int y = boundary.lowerLeft().getY(); y <= boundary.upperRight().getY(); y++) {
                addFieldsWithGrassGrowingChance(x, y);
            }
        }
    }

    protected void addFieldsWithGrassGrowingChance(int x, int y) {
//        usually used after eating grass - (zjesz trawe, to znowu jest szansa ze urosnie nastepna)
        Vector2d position = new Vector2d(x, y);
        if (isWithinEquator(position)) {
            fieldsWithGrassGrowPriority.add(position);
        } else {
            fieldsWithoutGrassGrowPriority.add(position);
        }
    }

    protected boolean isWithinEquator(Vector2d position) {
        int y = position.getY();
        int middleEquator = (boundary.upperRight().getY() + boundary.lowerLeft().getY() + 1) / 2;
        int equatorTop = middleEquator + middleEquator / 10;
        int equatorBottom = middleEquator - middleEquator / 10;
        return y >= equatorBottom && y <= equatorTop;
    }

    // grass management
    @Override
    public void dailyGrassGrow() {
        grassGrows(dailyAmountGrowingGrass);
    }

    @Override
    public void grassGrows(int grassAmount) {
        generateGrassFromGivenFields(this.fieldsWithGrassGrowPriority, grassAmount);
    }

    protected void generateGrassFromGivenFields(List<Vector2d> allFieldsWithPriority, int grassAmount) {
        for (int i = 0; i < grassAmount; i++) {
            if (allFieldsWithPriority.isEmpty() && fieldsWithoutGrassGrowPriority.isEmpty()) break;

            double priority = (Math.random() * 100);
            if (priority < 80 && !allFieldsWithPriority.isEmpty()) {
                grassGrowOnFields(allFieldsWithPriority);
            } else if (!fieldsWithoutGrassGrowPriority.isEmpty()) {
                grassGrowOnFields(fieldsWithoutGrassGrowPriority);
            }
        }
    }

    protected void grassGrowOnFields(List<Vector2d> fields) {
        int randomIndex = (int) (Math.random() * fields.size());
        Vector2d position = fields.get(randomIndex);
        grasses.put(position, new Grass(position));
        removeGrass(position);
    }

    protected void removeGrass(Vector2d position) {
        fieldsWithGrassGrowPriority.remove(position);
        fieldsWithoutGrassGrowPriority.remove(position);
    }

//    animal
    @Override
    public void allAnimalsAgeUp() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                animal.incrementAge();
            }
        }
    }

    protected void removeAnimal(Animal animal, Map<Vector2d, PriorityQueue<Animal>> animals) {
        Vector2d position = animal.position();
        animals.get(position).remove(animal);
        if (animals.get(position).isEmpty()) {
            animals.remove(position);
        }
    }

    @Override
    public boolean place(Animal animal) {
        if (!boundary.contains(animal.position())) {
            return false;
        }
        Vector2d position = animal.position();
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
            removeAnimal(animal, liveAnimals);
            animal.move();
            place(animal);
        }
    }

    @Override
    public void allAnimalsEat() {
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            if (!animals.isEmpty()) animalEat(animals.peek());
        }
    }

    protected void animalEat(Animal animal) {
        Vector2d position = animal.position();
        if (isGrassAt(position)) {
            animal.eat();
            grasses.remove(position);
            addFieldsWithGrassGrowingChance(position.getX(), position.getY());
        }
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
        moveAnimalsToDeadList(animalsToRemove);
    }

    protected void moveAnimalsToDeadList(List<Animal> animalsToRemove) {
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.position();
            removeAnimal(animal, liveAnimals);
            deadAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
            deadAnimals.get(position).add(animal);
        }
    }

    //    animal copulation
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

  
    private Animal copulate(Animal animal1, Animal animal2) {
        Vector2d position = animal1.position();
        Genome genome = switch (settings.isSpecialMutation()) {
            case SWAP -> new GenomeSwap(animal1, animal2, settings);
            case DEFAULT -> new Genome(animal1, animal2, settings);
        };
        Animal child = new Animal(genome, position, this.settings, this.currentDay);
        child.setParents(List.of(animal1, animal2));
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

    //    observers
    @Override
    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    // getters
    @Override
    public boolean isAnimalAt(Vector2d position) {
        return liveAnimals.containsKey(position);
    }

    @Override
    public boolean isDeadAnimalAt(Vector2d position) {
        return deadAnimals.containsKey(position);
    }

    @Override
    public boolean isGrassAt(Vector2d position) {
        return grasses.containsKey(position);
    }

    @Override
    public PriorityQueue<Animal> getAnimalsAt(Vector2d position) {
        return liveAnimals.get(position);
    }

    public Grass getGrassAt(Vector2d position) {
        return grasses.get(position);
    }

    public List<Vector2d> getFieldsWithGrassGrowPriority() {
        return fieldsWithGrassGrowPriority;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public Map<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public Map<Vector2d, PriorityQueue<Animal>> getLiveAnimals() {
        return liveAnimals;
    }

    public Map<Vector2d, PriorityQueue<Animal>> getDeadAnimals() {
        return deadAnimals;
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

    public MapObjectType getMapObjectType(Vector2d position) {
        if (isAnimalAt(position)) {
            return MapObjectType.ANIMAL;
        } else if (isGrassAt(position)) {
            return MapObjectType.GRASS;
        } else if (isDeadAnimalAt(position)) {
            return MapObjectType.DEAD_ANIMAL;
        } else {
            return MapObjectType.EMPTY;
        }
    }
}
