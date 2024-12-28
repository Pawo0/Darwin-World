package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genome {
    private List<Integer> genome;
    private int length;

    public Genome(int length) {
        Random random = new Random();
        this.length = length;
        genome = new ArrayList<Integer>();
        for (int i = 0; i < length; i++){
            genome.add(random.nextInt(8));
        }
    }


    public Genome(Animal parent1, Animal parent2) {
        Random random = new Random();
        int length = parent1.getGenotype().getLength();
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
            System.out.println("jestem po lewej");
        }
        else {
            rightSide = strongerParent.getGenome().subList(length-slicePoint, length);
            leftSide = otherParent.getGenome().subList(0, length-slicePoint);
        }
        genome.addAll(leftSide);
        genome.addAll(rightSide);
        mutate();
    }

    private void mutate(){

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

    public int getGenomSize() {
        return genome.size();
    }
}
