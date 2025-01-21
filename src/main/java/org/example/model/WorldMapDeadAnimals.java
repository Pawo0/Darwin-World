package org.example.model;

import org.example.simulations.SimulationSettings;

import java.util.*;

public class WorldMapDeadAnimals extends WorldMap {
    private final Map<Vector2d, PriorityQueue<Animal>> recentDeadAnimals;
    private final List<Vector2d> tmpFieldsWithPriority;
    private final int width;
    private final int height;


    public WorldMapDeadAnimals(SimulationSettings settings) {
        super(settings);
        this.width = settings.getMapWidth();
        this.height = settings.getMapHeight();
        this.tmpFieldsWithPriority = new ArrayList<>();
        this.recentDeadAnimals = new HashMap<>();
    }

    @Override
    public List<Vector2d> getFieldsWithGrassGrowPriority() {
        List<Vector2d> allFieldsWithPriority = new ArrayList<>();
        if (tmpFieldsWithPriority != null) allFieldsWithPriority.addAll(tmpFieldsWithPriority);
        if (fieldsWithGrassGrowPriority != null) allFieldsWithPriority.addAll(fieldsWithGrassGrowPriority);
        return allFieldsWithPriority;
    }

    @Override
    public void nextDay() {
        super.nextDay();
        clearDeadBodies();
    }

    public void clearDeadBodies() {
//    usuwanie starszych cial niz 10 dni, czyszczenie tmpFieldsWithPriority i dodanie ich na nowo (z obkrojonej listy)
        List<Animal> animalsToRemove = new ArrayList<>();
        for (PriorityQueue<Animal> animals : this.recentDeadAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getDeathDate() < this.currentDay - 10) {
//                    System.out.println("w dniu " + this.currentDay + " usunieto martwe zwierze umarte w " + animal.getDeathDate());
                    animalsToRemove.add(animal);
                }
            }
        }
//        nie wiem czy taka optymalna wersja usuwania ale cusz
        for (Animal animal : animalsToRemove) {
//            removeDeadAnimal(animal);
            removeAnimal(animal, recentDeadAnimals);
        }
        tmpFieldsWithPriority.clear();
        for (Vector2d position : this.recentDeadAnimals.keySet()) {
            addGrassPriorityAroundDeadBody(position);
        }
    }

    @Override
    protected void moveAnimalsToDeadList(List<Animal> animalsToRemove) {
        for (Animal animal : animalsToRemove) {
            Vector2d position = animal.getPosition();
            removeAnimal(animal, liveAnimals);
            deadAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
            deadAnimals.get(position).add(animal);
            recentDeadAnimals.putIfAbsent(position, new PriorityQueue<>(animalComparator));
            recentDeadAnimals.get(position).add(animal);
        }
    }

    private void addGrassPriorityAroundDeadBody(Vector2d position) {
        int x = position.getX();
        int y = position.getY();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < this.width && j >= 0 && j < this.height && !this.isGrassAt(new Vector2d(i, j)) && !this.tmpFieldsWithPriority.contains(new Vector2d(i, j))) {
                    tmpFieldsWithPriority.add(new Vector2d(i, j));
                }
            }
        }

    }

    //    mylące, bo w og mapie sprawdzamy czy kiedykolwiek cos tu zdechlo
    @Override
    public boolean isDeadAnimalAt(Vector2d position) {
        return recentDeadAnimals.containsKey(position);
    }

    private boolean isCloseToRecentDeadAnimal(Vector2d position) {
        if (this.recentDeadAnimals == null) return false;
        for (PriorityQueue<Animal> animals : this.recentDeadAnimals.values()) {
            for (Animal animal : animals) {
                if (position.getX() >= animal.getPosition().getX() - 2 && position.getX() <= animal.getPosition().getX() + 2 && position.getY() >= animal.getPosition().getY() - 2 && position.getY() <= animal.getPosition().getY() + 2) {
                    this.tmpFieldsWithPriority.add(position);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void addFieldsWithGrassGrowingChance(int x, int y) {
//        most often used after eating grass - (zjesz trawe, to znowu jest szansa ze urosnie nastepna)
        super.addFieldsWithGrassGrowingChance(x, y);
        Vector2d position = new Vector2d(x, y);
        if (isCloseToRecentDeadAnimal(position)) {
            addGrassPriorityAroundDeadBody(position);
        }
    }

    @Override
    public void grassGrows(int grassAmount) {
        List<Vector2d> allFieldsWithPriority = getFieldsWithGrassGrowPriority();
        generateGrassFromGivenFields(allFieldsWithPriority, grassAmount);
    }

    @Override
    protected void removeGrass(Vector2d position) {
        if (tmpFieldsWithPriority != null) tmpFieldsWithPriority.remove(position);
        super.removeGrass(position);
    }

    public List<Vector2d> getTmpFieldsWithPriority() {
        return tmpFieldsWithPriority;
    }
}
