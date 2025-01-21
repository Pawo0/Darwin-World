package org.example.model;

import org.example.genomes.Genome;
import org.example.genomes.MutationType;
import org.example.map.WorldMap;
import org.example.map.objects.Animal;
import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    SimulationSettings settings = new SimulationSettings(10, 10, 0, 20, 1, false, 1, 100, 10, 1,0,0, MutationType.DEFAULT, 5, 400, false);
    Genome genome = new Genome(settings);

    @Test
    void placeAnimalSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings, 0);
        assertTrue(worldMap.place(animal));
        assertTrue(worldMap.isAnimalAt(new Vector2d(1, 1)));
    }

    @Test
    void placeAnimalAtOccupiedPosition() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal1 = new Animal(genome, new Vector2d(1, 1), settings, 0);
        Animal animal2 = new Animal(genome, new Vector2d(1, 1), settings, 0);
        worldMap.place(animal1);
        assertTrue(worldMap.place(animal2));
        assertEquals(2, worldMap.getAnimalsAt(new Vector2d(1, 1)).size());
    }

    @Test
    void allAnimalsMoveSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings, 0);
        worldMap.place(animal);

        Animal animal2 = new Animal(genome, new Vector2d(2, 2), settings, 0);
        worldMap.place(animal2);


        worldMap.nextDay();


        assertNotEquals(new Vector2d(1, 1), animal.position());
    }

    @Test
    void allAnimalsEatSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Vector2d position = new Vector2d(3, 4);
        Animal animal = new Animal(genome, position, settings, 0);
        worldMap.place(animal);
        if(worldMap.isGrassAt(position)) {
            worldMap.allAnimalsEat();
            assertEquals(120, animal.getEnergy());
        }
        else {
            worldMap.allAnimalsEat();
            assertEquals(100, animal.getEnergy());
        }
    }

    @Test
    void grassGrowsSuccessfully() {
        WorldMap worldMap = new WorldMap(settings);
        worldMap.grassGrows(1);
        assertFalse(worldMap.getGrasses().isEmpty());
    }

    @Test
    void animalCopulateSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal1 = new Animal(genome, new Vector2d(1, 1), settings, 0);
        Animal animal2 = new Animal(genome, new Vector2d(1, 1), settings, 0);
        worldMap.place(animal1);
        worldMap.place(animal2);
        worldMap.animalCopulate();
        assertEquals(3, worldMap.getAnimalsAt(new Vector2d(1, 1)).size());
    }

    @Test
    void checkForDeadAnimalsSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings, 0);
        worldMap.place(animal);
        animal.subtractCopulationEnergy(101);
        worldMap.checkForDeadAnimals();
        assertTrue(worldMap.isDeadAnimalAt(new Vector2d(1, 1)));
    }
}