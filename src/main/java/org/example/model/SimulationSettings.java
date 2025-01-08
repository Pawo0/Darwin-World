package org.example.model;

public class SimulationSettings {
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    private final int startAmountOfGrass;
    private final int energyGainedFromEating;
    private final int dailyAmountGrowingGrass;
    private final boolean lifeGivingCorpses;
    private final int startAmountOfAnimals;
    private final int startAnimalEnergy;
    private final int energyNeededToCopulate;
    private final int energyUsedToCopulate;
    private final int minMutationAmount;
    private final int maxMutationAmount;
    private final boolean specialMutation;
    private final int genomeLength;


    public SimulationSettings(int mapWidth, int mapHeight, int startAmountOfGrass, int energyGainedFromEating, int dailyAmountGrowingGrass, boolean lifeGivingCorpses, int startAmountOfAnimals, int startAnimalEnergy, int energyNeededToCopulate, int energyUsedToCopulate, int minMutationAmount, int maxMutationAmount, boolean specialMutation, int genomeLength) {
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(mapWidth - 1, mapHeight - 1);
        this.startAmountOfGrass = startAmountOfGrass;
        this.energyGainedFromEating = energyGainedFromEating;
        this.dailyAmountGrowingGrass = dailyAmountGrowingGrass;
        this.lifeGivingCorpses = lifeGivingCorpses;
        this.startAmountOfAnimals = startAmountOfAnimals;
        this.startAnimalEnergy = startAnimalEnergy;
        this.energyNeededToCopulate = energyNeededToCopulate;
        this.energyUsedToCopulate = energyUsedToCopulate;
        this.minMutationAmount = minMutationAmount;
        this.maxMutationAmount = maxMutationAmount;
        this.specialMutation = specialMutation;
        this.genomeLength = genomeLength;
    }

    public Boundary getBoundary() {
        return new Boundary(lowerLeft, upperRight);
    }

    public int getStartAmountOfGrass() {
        return startAmountOfGrass;
    }

    public int getEnergyGainedFromEating() {
        return energyGainedFromEating;
    }

    public int getDailyAmountGrowingGrass() {
        return dailyAmountGrowingGrass;
    }

    public boolean isLifeGivingCorpses() {
        return lifeGivingCorpses;
    }

    public int getStartAmountOfAnimals() {
        return startAmountOfAnimals;
    }

    public int getStartAnimalEnergy() {
        return startAnimalEnergy;
    }

    public int getEnergyNeededToCopulate() {
        return energyNeededToCopulate;
    }

    public int getEnergyUsedToCopulate() {
        return energyUsedToCopulate;
    }

    public int getMinMutationAmount() {
        return minMutationAmount;
    }

    public int getMaxMutationAmount() {
        return maxMutationAmount;
    }

    public boolean isSpecialMutation() {
        return specialMutation;
    }

    public int getGenomeLength() {
        return genomeLength;
    }
}
