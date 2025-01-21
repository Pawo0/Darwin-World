package org.example.model;

import org.example.genomes.Genome;
import org.example.genomes.MutationType;
import org.example.map.WorldMap;
import org.example.map.WorldMapDeadAnimals;
import org.example.map.objects.Animal;
import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapDeadAnimalsTest {

    SimulationSettings settings = new SimulationSettings(10, 10, 0, 20, 1, false, 1, 100, 10, 1, 0, 0, MutationType.DEFAULT, 5, 400, false);
    Genome genome = new Genome(settings);


    @Test
    void grassGrowsOnceOnEveryField() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        int initialGrassCount = map.getGrasses().size();

        map.grassGrows(80);

        int updatedGrassCount = map.getGrasses().size();
        assertEquals(initialGrassCount + 80, updatedGrassCount);
    }

    @Test
    void grassNotGrowsOnTooManyField() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        int initialGrassCount = map.getGrasses().size();

        map.grassGrows(240);

        int updatedGrassCount = map.getGrasses().size();
        System.out.println(updatedGrassCount);
        assertEquals(initialGrassCount + 100, updatedGrassCount);
    }

    @Test
    void losePriorityAfterGrassGrow() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);

        map.grassGrows(1);
        List<Vector2d> priorityFields = map.getFieldsWithGrassGrowPriority();
        boolean isGrassOnPriority = priorityFields.stream()
                .anyMatch(map::isGrassAt);

        assertFalse(isGrassOnPriority);
    }


    @Test
    void clearDeadBodies_doesNotRemoveRecentDeadAnimals() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        Animal recentDeadAnimal = new Animal(genome, new Vector2d(0, 0), settings, 0);
        map.getDeadAnimals().putIfAbsent(new Vector2d(0, 0), new PriorityQueue<>());

        for (int i = 0; i < 11; i++) {
            map.nextDay();
        }
        map.clearDeadBodies();

        assertFalse(map.getDeadAnimals().get(new Vector2d(0, 0)).contains(recentDeadAnimal));
    }

    @Test
    void addGrassPriorityAroundDeadBody_addsPriorityFields() {
        SimulationSettings DeadAnimalsSettings = new SimulationSettings(10, 10, 0, 0, 0, true, 1, 0, 10, 1, 0, 0, MutationType.DEFAULT, 5, 400, false);
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(DeadAnimalsSettings);
        Animal animal = new Animal(genome, new Vector2d(5, 5), DeadAnimalsSettings, 0);

        map.place(animal);

        map.nextDay();
        assertFalse(map.getTmpFieldsWithPriority().contains(animal.position()));

        map.nextDay();
        assertTrue(map.getTmpFieldsWithPriority().contains(animal.position()));
    }

}