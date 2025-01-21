package org.example.model;

import org.example.genomes.Genome;
import org.example.genomes.MutationType;
import org.example.map.WorldMapDeadAnimals;
import org.example.map.objects.Animal;
import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapDeadAnimalsTest {

    SimulationSettings settings = new SimulationSettings(10, 10, 0, 20, 1, false, 1, 100, 10, 1, 0, 0, MutationType.DEFAULT, 5, 400, false);
    Genome genome = new Genome(settings);

    @Test
    void clearDeadBodies_removesOldDeadAnimals() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        Animal oldDeadAnimal = new Animal(genome, new Vector2d(0, 0), settings,0 );
        map.getDeadAnimals().putIfAbsent(new Vector2d(0, 0), new PriorityQueue<>());
        for (int i = 0; i < 11; i++) {
            map.nextDay();
        }

        map.clearDeadBodies();

        assertFalse(map.getDeadAnimals().get(new Vector2d(0, 0)).contains(oldDeadAnimal));
    }

    @Test
    void clearDeadBodies_doesNotRemoveRecentDeadAnimals() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        Animal recentDeadAnimal = new Animal(genome, new Vector2d(0, 0), settings, 0);
        map.getDeadAnimals().putIfAbsent(new Vector2d(0, 0), new PriorityQueue<>());
        for (int i = 0; i < 5; i++) {
            map.nextDay();
        }

        map.clearDeadBodies();

        assertFalse(map.getDeadAnimals().get(new Vector2d(0, 0)).contains(recentDeadAnimal));
    }

    @Test
    void addGrassPriorityAroundDeadBody_addsPriorityFields() {
        SimulationSettings DeadAnimalsSettings = new SimulationSettings(10, 10, 0, 0, 0, true, 1, 0, 10, 1, 0, 0, MutationType.DEFAULT, 5, 400, false);



        WorldMapDeadAnimals map = new WorldMapDeadAnimals(DeadAnimalsSettings);
        Vector2d deadBodyPosition = new Vector2d(5, 5);
        map.getDeadAnimals().putIfAbsent(deadBodyPosition, new PriorityQueue<>());
        map.place(new Animal(genome, deadBodyPosition, DeadAnimalsSettings, 0));

        map.nextDay();
        map.nextDay();

        assertTrue(map.getTmpFieldsWithPriority().contains(new Vector2d(5, 5)));
    }

    @Test
    void grassGrows_addsGrassToPriorityFields() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        map.getTmpFieldsWithPriority().add(new Vector2d(1, 1));
        map.getFieldsWithGrassGrowPriority().add(new Vector2d(2, 2));

        map.grassGrows(100);

        assertTrue(map.isGrassAt(new Vector2d(1, 1)));
        assertTrue(map.isGrassAt(new Vector2d(2, 2)));
    }

    @Test
    void removeGrassFromFields_removesGrass() {
        WorldMapDeadAnimals map = new WorldMapDeadAnimals(settings);
        Vector2d position = new Vector2d(1, 1);
        map.grassGrows(100);

        map.removeGrass(position);

        assertFalse(map.getTmpFieldsWithPriority().contains(position));
        assertFalse(map.isGrassAt(position));
    }
}