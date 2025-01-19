package org.example.model;

import org.example.simulations.SimulationSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class WorldMapDeadAnimals extends WorldMap {
// todo miec liste z martwymi zwierzakami jak w WorldMap i  dodatkowa martwych zwierzat, ktore beda sie rozkladac po x dniach
    private final List<Vector2d> tmpFieldsWithPriority = new ArrayList<>();
    private int width;
    private int height;

    public WorldMapDeadAnimals(SimulationSettings settings) {
        super(settings);
        this.width = settings.getMapWidth();
        this.height = settings.getMapHeight();
    }

    @Override
    public void nextDay() {
        super.nextDay();
        clearDeadBodies();
    }

    public void clearDeadBodies() {
//    usuwanie starszych cial niz 5 dni, czyszczenie tmpFieldsWithPriority i dodanie ich na nowo (z obkrojonej listy)
        List<Animal> animalsToRemove = new ArrayList<>();
        for (PriorityQueue<Animal> animals : this.deadAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getDeathDate() < this.currentDay - 10) {
//                    System.out.println("w dniu " + this.currentDay + " usunieto martwe zwierze umarte w " + animal.getDeathDate());
                    animalsToRemove.add(animal);
                }
            }
        }
//        nie wiem czy taka optymalna wersja usuwania ale cusz
        for (Animal animal : animalsToRemove) {
            removeDeadAnimal(animal);
        }
        tmpFieldsWithPriority.clear();
        for (Vector2d position : this.deadAnimals.keySet()) {
            addGrassPriorityAroundDeadBody(position);
        }
    }

    private void addGrassPriorityAroundDeadBody(Vector2d position){
        int x = position.getX();
        int y = position.getY();
        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                if (i >= 0 && i < this.width && j >= 0 && j < this.height && !this.isGrassAt(new Vector2d(i, j)) && !this.tmpFieldsWithPriority.contains(new Vector2d(i, j))){
                    tmpFieldsWithPriority.add(new Vector2d(i, j));
                }
            }
        }

    }


    private boolean isCloseToDeadAnimal(Vector2d position) {
        for (PriorityQueue<Animal> animals : this.deadAnimals.values()) {
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
        if (isCloseToDeadAnimal(position)) {
            addGrassPriorityAroundDeadBody(position);
        }
    }

    @Override
    public void grassGrows(int grassAmount) {
        List<Vector2d> allFieldsWithPriority = new ArrayList<>();
        if (tmpFieldsWithPriority != null) allFieldsWithPriority.addAll(tmpFieldsWithPriority);
        if (fieldsWithGrassGrowPriority != null) allFieldsWithPriority.addAll(fieldsWithGrassGrowPriority);
        generateGrassFromGivenFields(allFieldsWithPriority, grassAmount);
    }

    @Override
    protected void removeGrassFromFields(Vector2d position){
        if (tmpFieldsWithPriority != null) tmpFieldsWithPriority.remove(position);
        super.removeGrassFromFields(position);
    }
}
