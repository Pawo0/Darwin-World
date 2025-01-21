package org.example.model;

import org.example.genomes.Genome;
import org.example.genomes.MutationType;
import org.example.map.WorldMap;
import org.example.map.objects.Animal;
import org.example.simulations.SimulationSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    SimulationSettings settings = new SimulationSettings(10, 10, 0, 20, 1, false, 1, 100, 10, 1,0,0, MutationType.DEFAULT, 5, 400, false);
    Genome genome = new Genome(settings);

    @Test
    void mapInitializationWithGrass() {
        // Given
        WorldMap worldMap = new WorldMap(settings);

        // When
        int initialGrassCount = worldMap.getGrasses().size();

        // Then
        assertEquals(settings.getStartAmountOfGrass(), initialGrassCount);
    }


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
        Vector2d position = new Vector2d(5, 5);
        Animal animal = new Animal(genome, position, settings, 0);

        worldMap.grassGrows(20);
        boolean isGrassAtPosition = worldMap.isGrassAt(position);
        worldMap.place(animal);
        worldMap.allAnimalsEat();

        if(isGrassAtPosition) {
            System.out.println(animal.getEnergy());
            assertEquals(120, animal.getEnergy());
        }
        else {
            assertEquals(100, animal.getEnergy());
        }
    }

    @Test
    void grassGrowsOnceOnEveryField() {
        WorldMap worldMap = new WorldMap(settings);
        int initialGrassCount = worldMap.getGrasses().size();

        worldMap.grassGrows(80);

        int updatedGrassCount = worldMap.getGrasses().size();
        assertEquals(initialGrassCount + 80, updatedGrassCount);
    }

    @Test
    void grassNotGrowsOnTooManyField() {
        WorldMap worldMap = new WorldMap(settings);
        int initialGrassCount = worldMap.getGrasses().size();

        worldMap.grassGrows(240);

        int updatedGrassCount = worldMap.getGrasses().size();
        assertEquals(initialGrassCount + 100, updatedGrassCount);
    }

    @Test
    void losePriorityAfterGrassGrow() {
        WorldMap worldMap = new WorldMap(settings);
        List<Vector2d> priorityFields = worldMap.getFieldsWithGrassGrowPriority();

        worldMap.grassGrows(1);

        boolean isGrassOnPriority = priorityFields.stream()
                .anyMatch(worldMap::isGrassAt);
        assertFalse(isGrassOnPriority);
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