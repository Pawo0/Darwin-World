package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class WorldMapDeadAnimals extends WorldMap {

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
        for (PriorityQueue<Animal> animals : this.deadAnimals.values()) {
            for (Animal animal : animals) {
                if (animal.getDeathDate() < this.currentDay - 10) {
                    System.out.println("Usuwam martwe zwierze");
                    System.out.println(animal.getDeathDate());
                    System.out.println(this.currentDay);
                    this.deadAnimals.get(animal.getPosition()).remove(animal);
                }
            }
        }
        tmpFieldsWithPriority.clear();
        for (Vector2d position : this.deadAnimals.keySet()) {
            updateGrassPriorityAroundDeadBody(position, true);
        }
    }

    private void updateGrassPriorityAroundDeadBody(Vector2d position, boolean add){
//        add - true jesli dodajemy pola w kwadracie wokol trupa, false jesli usuwamy
        int x = position.getX();
        int y = position.getY();
        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                if (add && i >= 0 && i < this.width && j >= 0 && j < this.height && !this.isGrassAt(new Vector2d(i, j)) && !this.fieldsWithGrassGrowPriority.contains(new Vector2d(i, j))){
                    tmpFieldsWithPriority.add(new Vector2d(i, j));
                } else if (!add && i >= 0 && i < this.width && j >= 0 && j < this.height){
//                    jesli pole aktualnie nie jest uwzgledniane, remove nic nie zrobi
                    tmpFieldsWithPriority.remove(new Vector2d(i, j));
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
            updateGrassPriorityAroundDeadBody(position, true);
        }
    }

    @Override
    public void grassGrows(int grassAmount) {
//        prawdopodonie mozna to zrobic lepiej, np tylko w worldmap z uzyciem instanceof
        int i = 0;
        List<Vector2d> allFieldsWithPriority = new ArrayList<>();
        if (tmpFieldsWithPriority != null) allFieldsWithPriority.addAll(tmpFieldsWithPriority);
        if (fieldsWithGrassGrowPriority != null) allFieldsWithPriority.addAll(fieldsWithGrassGrowPriority);
        while (i < grassAmount) {
            if (allFieldsWithPriority.isEmpty()) {
                if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                    break;
                } else {
                    grassGrowOnFields(fieldsWithoutGrassGrowPriority);
                }
            } else if (fieldsWithoutGrassGrowPriority.isEmpty()) {
                grassGrowOnFields(allFieldsWithPriority);
            } else {
                double priority = (Math.random() * 100);
                if (priority <= 80) {
                    grassGrowOnFields(allFieldsWithPriority);
                } else {
                    grassGrowOnFields(fieldsWithoutGrassGrowPriority);
                }
            }
            i++;
        }
    }

}
