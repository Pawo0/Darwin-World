package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    SimulationSettings settings = new SimulationSettings(10, 10, 0, 0, 1, false, 1, 3, 10, 1,0,0,false, 5);
    Genome genome = new Genome(settings);

    @Test
    void placeAnimalSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings);
        assertTrue(worldMap.place(animal));
        assertTrue(worldMap.isAnimalAt(new Vector2d(1, 1)));
    }

    @Test
    void placeAnimalAtOccupiedPosition() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal1 = new Animal(genome, new Vector2d(1, 1), settings);
        Animal animal2 = new Animal(genome, new Vector2d(1, 1), settings);
        worldMap.place(animal1);
        assertTrue(worldMap.place(animal2));
        assertEquals(2, worldMap.animalsAt(new Vector2d(1, 1)).size());
    }

    @Test
    void allAnimalsMoveSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings);
        worldMap.place(animal);
        worldMap.allAnimalsMove();
        assertNotEquals(new Vector2d(1, 1), animal.getPosition());
    }

    @Test
    void allAnimalsEatSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Vector2d position = new Vector2d(3, 4);
        Animal animal = new Animal(genome, position, settings);
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
        assertFalse(worldMap.getGrasses().isEmpty());
    }

    @Test
    void animalCopulateSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal1 = new Animal(genome, new Vector2d(1, 1), settings);
        Animal animal2 = new Animal(genome, new Vector2d(1, 1), settings);
        worldMap.place(animal1);
        worldMap.place(animal2);
        worldMap.animalCopulate();
        assertEquals(3, worldMap.animalsAt(new Vector2d(1, 1)).size());
    }

    @Test
    void checkForDeadAnimalsSuccessfully() throws IncorrectPositionException {
        WorldMap worldMap = new WorldMap(settings);
        Animal animal = new Animal(genome, new Vector2d(1, 1), settings);
        worldMap.place(animal);
        animal.subtractCopulationEnergy(101);
        worldMap.checkForDeadAnimals();
        assertTrue(worldMap.isDeadAnimalAt(new Vector2d(1, 1)));
    }
}