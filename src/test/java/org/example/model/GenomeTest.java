package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    private Genome genome;
    private final int genomeLength = 10;

    @BeforeEach
    void setUp() {
        genome = new Genome(genomeLength);
    }

    @Test
    void testGenomeInitialization() {
        List<Integer> genes = genome.getGenome();

        assertNotNull(genes, "Genome list should not be null.");
        assertEquals(genomeLength, genes.size(), "Genome should have the correct length.");
        genes.forEach(gen -> assertTrue(gen >= 0 && gen <= 7, "Each gene should be between 0 and 7."));
    }

    @Test
    void testGetLength() {
        assertEquals(genomeLength, genome.getLength());
    }

    @Test
    void testGetGenomSize() {
        assertEquals(genomeLength, genome.getGenomSize());
    }

    @Test
    void testGetGen() {
        int geneIndex = 5;
        int gene = genome.getGen(geneIndex);
        assertTrue(gene >= 0 && gene <= 7, "Gene should be between 0 and 7.");
    }

    @Test
    void testParentConstructor() {
        int genomeLength = 10;
        Genome parent1 = new Genome(genomeLength);
        Genome parent2 = new Genome(genomeLength);

        Animal animal1 = new Animal(parent1, new Vector2d(0, 0));
        animal1.substractCopulationEnergy(30); // parent1: energy = 70
        Animal animal2 = new Animal(parent2, new Vector2d(0, 0));
        animal2.substractCopulationEnergy(50); // parent2: energy = 50


        Genome childGenome = new Genome(animal1, animal2);
        List<Integer> childGenes = childGenome.getGenome();


        float parent1EnergyRatio = (float) animal1.getEnergy() / (animal1.getEnergy() + animal2.getEnergy());
        int parent1SlicePoint = (int) (parent1EnergyRatio * genomeLength);


        List<Integer> parent1Genes = parent1.getGenome();
        List<Integer> parent2Genes = parent2.getGenome();


        boolean parent1OnLeft = childGenes.subList(0, parent1SlicePoint).equals(parent1Genes.subList(0, parent1SlicePoint)) &&
                childGenes.subList(parent1SlicePoint, genomeLength).equals(parent2Genes.subList(parent1SlicePoint, genomeLength));

        boolean parent1OnRight = childGenes.subList(0, genomeLength - parent1SlicePoint).equals(parent2Genes.subList(0, genomeLength - parent1SlicePoint)) &&
                childGenes.subList(genomeLength - parent1SlicePoint, genomeLength).equals(parent1Genes.subList(genomeLength - parent1SlicePoint, genomeLength));

        // Końcowe asercje
        assertTrue(parent1OnLeft || parent1OnRight);
    }



}