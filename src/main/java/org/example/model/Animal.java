package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Animal implements WorldElement {
    private final static int ENERGY_FROM_EATING = 20;

    private Vector2d position;
    private int energy;
    private int childrenCounter;
    private int age;
    private List<Animal> children;
    private int deathDate;
    private MapDirection mapDirection;
    private Genome genotype;
    private int geneIndex;
    private int grassEaten;


    public Animal(Genome genotype, Vector2d position) {
        this.position = position;
        this.energy = 100;
        this.childrenCounter = 0;
        this.age = 0;
        this.children = new ArrayList<>();

        this.mapDirection = MapDirection.NORTH;
        this.genotype = genotype;
        this.geneIndex = 0;
        this.grassEaten = 0;
        this.deathDate = -1;
    }


    public void move() {
        MapDirection newDirection = this.mapDirection.rotate(genotype.getGen(geneIndex%genotype.getGenomSize()));
        this.mapDirection = newDirection;
        this.position = this.position.add(mapDirection.toUnitVector());
        this.geneIndex++;
        this.energy--;
    }

    public void birthChild(Animal child, int energyToSubstract) {
        this.children.add(child);
        this.childrenCounter++;
        this.energy -= energyToSubstract;
    }

    public void eat(){
        this.energy += ENERGY_FROM_EATING;
        this.grassEaten++;
    }

    public void incrementAge(){
        this.age++;
    }

    public void animalDeath(){
        this.deathDate=this.age;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public int getChildrenCounter() {
        return childrenCounter;
    }

    public int getAge() {
        return age;
    }

    public List<Animal> getChildren() {
        return children;
    }

    public MapDirection getMapDirection() {
        return mapDirection;
    }

    public Genome getGenotype() {
        return genotype;
    }

    public int getGeneIndex() {
        return geneIndex;
    }

    public int getGrassEaten() {
        return grassEaten;
    }

    public int getDeathDate() {
        return deathDate;
    }

    public void substractCopulationEnergy(int energyToSubstract) {
        this.energy -= energyToSubstract;
    }
}
