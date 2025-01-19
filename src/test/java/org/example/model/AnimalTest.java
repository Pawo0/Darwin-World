package org.example.model;

import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    SimulationSettings settings = new SimulationSettings(10, 10, 0, 20, 1, false, 1, 100, 10, 1,0,0,MutationType.DEFAULT, 5, 400);

    @Test
    void testAnimalInitialization() {
        Genome genome = new Genome(settings);
        Vector2d initialPosition = new Vector2d(0, 0);
        Animal animal = new Animal(genome, initialPosition, settings,0);

        assertEquals(initialPosition, animal.getPosition());
        assertEquals(100, animal.getEnergy());
        assertEquals(0, animal.getChildrenCounter());
        assertEquals(0, animal.getAge());
        assertEquals(0, animal.getGrassEaten());
        assertEquals(-1, animal.getDeathDate());
        assertEquals(MapDirection.NORTH, animal.getMapDirection());
        assertEquals(genome, animal.getGenotype());
    }

    @Test
    void testMove() {
        Genome genome = new Genome(settings);
        Vector2d initialPosition = new Vector2d(0, 0);
        Animal animal = new Animal(genome, initialPosition, settings, 0);

        animal.move();
        assertNotEquals(initialPosition, animal.getPosition());
        assertEquals(99, animal.getEnergy());
        assertEquals(1, animal.getGeneIndex());
    }

    @Test
    void testEat() {
        Genome genome = new Genome(settings);
        Vector2d position = new Vector2d(0, 0);
        Animal animal = new Animal(genome, position, settings, 0);

        animal.eat();
        assertEquals(120, animal.getEnergy());
        assertEquals(1, animal.getGrassEaten());
    }

    @Test
    void testIncrementAge() {
        Genome genome = new Genome(settings);
        Vector2d position = new Vector2d(0, 0);
        Animal animal = new Animal(genome, position, settings, 0);

        animal.incrementAge();
        assertEquals(1, animal.getAge());
    }

    @Test
    void testAnimalDeath() {
        Genome genome = new Genome(settings);
        Vector2d position = new Vector2d(0, 0);
        Animal animal = new Animal(genome, position, settings, 0);

        animal.incrementAge();
        animal.incrementAge();
        animal.animalDeath();

        assertEquals(2, animal.getDeathDate());
    }


    @Test
    void testChildrenManagement() {
        Genome genome = new Genome(settings);
        Vector2d position = new Vector2d(0, 0);
        Animal parent = new Animal(genome, position, settings, 0);

        Animal child = new Animal(genome, position, settings, 0);
        parent.birthChild(child, 30);

        assertEquals(70, parent.getEnergy());
        assertEquals(1, parent.getChildren().size());
        assertTrue(parent.getChildren().contains(child));
    }

}
