package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genome {
    private List<Integer> genome;

    public Genome(int length) {
        Random random = new Random();
        genome = new ArrayList<Integer>();
        for (int i = 0; i < length; i++){
            genome.set(i, random.nextInt(0, 7));
        }
    }

    public Genome(Genome parent1Genome, Genome parent2Genome, int length) {
        Random random = new Random();
        genome = new ArrayList<Integer>();
        for (int i = 0; i < length; i++){
            genome.set(i, random.nextInt(0, 7));
        }
    }

    public int getGen(int genIndex) {
        return genome.get(genIndex);
    }

    public int getGenomSize() {
        return genome.size();
    }
}
