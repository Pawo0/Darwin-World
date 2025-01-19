package org.example.model;

public class AnimalStats {
    private final Genome genome;
    private final int currentGenome;
    private final int currentEnergy;
    private final int eatenGrass;
    private final int childrenAmount;
    private final int descendantsAmount;
    private final int lifeSpan;
    private final int deathDay;

    public AnimalStats(Animal animal){
        this.genome = animal.getGenotype();
        this.currentGenome = this.genome.getGen(animal.getGeneIndex());
        this.currentEnergy = animal.getEnergy();
        this.eatenGrass = animal.getGrassEaten();
        this.childrenAmount = animal.getChildrenCounter();
        this.descendantsAmount = animal.getDescendantsCounter();
        this.lifeSpan = animal.getAge();
        this.deathDay = animal.getDeathDate();
    }

    public Genome getGenome() {
        return genome;
    }

    public int getCurrentGenome() {
        return currentGenome;
    }

    public int getEatenGrass() {
        return eatenGrass;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getChildrenAmount() {
        return childrenAmount;
    }

    public int getDescendantsAmount() {
        return descendantsAmount;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public int getDeathDay() {
        return deathDay;
    }
}
