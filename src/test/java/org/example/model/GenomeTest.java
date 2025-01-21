package org.example.model;

import org.example.genomes.Genome;
import org.example.genomes.MutationType;
import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {
    SimulationSettings settings = new SimulationSettings(10, 10, 0, 0, 1, false, 1, 3, 10, 1,0,1, MutationType.DEFAULT, 10, 400, false);

    private Genome genome;

    @BeforeEach
    void setUp() {
        genome = new Genome(settings);
    }

    @Test
    void testGenomeInitialization() {
        List<Integer> genes = genome.getGenome();

        assertNotNull(genes, "Genome list should not be null.");
        assertEquals(settings.getGenomeLength(), genes.size(), "Genome should have the correct length.");
        genes.forEach(gen -> assertTrue(gen >= 0 && gen <= 7, "Each gene should be between 0 and 7."));
    }

    @Test
    void testGetLength() {
        assertEquals(settings.getGenomeLength(), genome.getLength());
    }

    @Test
    void testGetGenomeSize() {
        assertEquals(settings.getGenomeLength(), genome.getGenomeSize());
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
        Genome parent1 = new Genome(settings);
        Genome parent2 = new Genome(settings);

        Animal animal1 = new Animal(parent1, new Vector2d(0, 0), settings, 0);
        animal1.subtractCopulationEnergy(30); // parent1: energy = 70
        Animal animal2 = new Animal(parent2, new Vector2d(0, 0), settings, 0);
        animal2.subtractCopulationEnergy(50); // parent2: energy = 50


        Genome childGenome = new Genome(animal1, animal2, settings);
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