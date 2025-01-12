package org.example.model;

import org.example.simulations.SimulationSettings;

import java.util.Random;

public class GenomeSwap extends Genome{
    public GenomeSwap(SimulationSettings settings) {
        super(settings);
    }

    public GenomeSwap(Animal parent1, Animal parent2, SimulationSettings settings) {
        super(parent1, parent2, settings);
    }

    @Override
    protected void mutate() {
        Random random = new Random();
        int numberOfMutations = random.nextInt(this.getLength()-1);
        for (int i = 0; i < numberOfMutations; i++){
            boolean isSwap = random.nextBoolean();
            if (isSwap){
                int geneIndex1 = random.nextInt(this.getLength());
                int geneIndex2 = random.nextInt(this.getLength());
                int geneValue1 = this.getGenome().get(geneIndex1);
                int geneValue2 = this.getGenome().get(geneIndex2);
                this.getGenome().set(geneIndex1, geneValue2);
                this.getGenome().set(geneIndex2, geneValue1);
            }
            else {
                int geneIndex = random.nextInt(this.getLength());
                int geneValue = random.nextInt(8);
                this.getGenome().set(geneIndex, geneValue);
            }
        }
    }
}
