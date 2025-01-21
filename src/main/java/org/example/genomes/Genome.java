package org.example.genomes;

import org.example.map.objects.Animal;
import org.example.simulations.SimulationSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Genome {
    private List<Integer> genome;
    private int length;
    protected SimulationSettings settings;

    public Genome(SimulationSettings settings) {
        this.settings = settings;
        Random random = new Random();
        this.length = settings.getGenomeLength();
        genome = new ArrayList<Integer>();
        for (int i = 0; i < length; i++){
            genome.add(random.nextInt(8));
        }
    }


    public Genome(Animal parent1, Animal parent2, SimulationSettings settings){
        Random random = new Random();
        this.settings = settings;
        int length = settings.getGenomeLength();
        genome = new ArrayList<Integer>();
        int side = random.nextInt(2); // 0 - left 1 - right
        int totalEnergy = parent1.getEnergy() + parent2.getEnergy();

        Genome strongerParent = parent1.getGenotype();
        Genome otherParent = parent2.getGenotype();

        int biggerEnergy = parent1.getEnergy();
        if (parent1.getEnergy() < parent2.getEnergy()){
            strongerParent = parent2.getGenotype();
            otherParent = parent1.getGenotype();
            biggerEnergy = parent2.getEnergy();
        }
        float ratio = (float) biggerEnergy / totalEnergy;
        int slicePoint = (int)(ratio * length);

        List<Integer> leftSide;
        List<Integer> rightSide;
        if (side == 0){
            leftSide = strongerParent.getGenome().subList(0, slicePoint);
            rightSide = otherParent.getGenome().subList(slicePoint, length);
        }
        else {
            rightSide = strongerParent.getGenome().subList(length-slicePoint, length);
            leftSide = otherParent.getGenome().subList(0, length-slicePoint);
        }
        genome.addAll(leftSide);
        genome.addAll(rightSide);
        this.mutate();
    }

    protected void mutate(){
        Random random = new Random();
        int numberOfMutations = random.nextInt(settings.getMinMutationAmount(),settings.getMaxMutationAmount());
        for (int i = 0; i < numberOfMutations; i++){
            int geneIndex = random.nextInt(genome.size());
            int geneValue = random.nextInt(8);
            this.getGenome().set(geneIndex, geneValue);
        }
    }

    public List<Integer> getGenome() {
        return genome;
    }

    public int getLength() {
        return length;
    }

    public int getGen(int genIndex) {
        return genome.get(genIndex);
    }

    public int getGenomeSize() {
        return genome.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genome genome1 = (Genome) o;
        return Objects.equals(genome, genome1.genome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genome);
    }

    @Override
    public String toString() {
        return genome.toString();
    }
}
