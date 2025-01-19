package org.example.simulations;

import org.example.model.Animal;
import org.example.model.Genome;
import org.example.model.Vector2d;
import org.example.model.WorldMap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SimulationStats {
    private final WorldMap map;
    private final Map<Vector2d, PriorityQueue<Animal>> liveAnimals;
    private final Map<Vector2d, PriorityQueue<Animal>> deadAnimals;

    private int day;
    private int deadAnimalsAmount;
    private int liveAnimalsAmount;
    private int grassAmount;
    private Genome dominantGenome;
    private double averageEnergy;
    private double averageLifeSpan;
//    private int averageAge;
    private double averageDescendantAmount;

    public SimulationStats(WorldMap map) {
        this.map = map;
        this.liveAnimals = map.getLiveAnimals();
        this.deadAnimals = map.getDeadAnimals();

        this.day = map.getCurrentDay();
        this.liveAnimalsAmount = liveAnimals.values().stream().mapToInt(PriorityQueue::size).sum();
        this.deadAnimalsAmount = deadAnimals.values().stream().mapToInt(PriorityQueue::size).sum();
        this.grassAmount = map.getGrasses().size();
        this.calculateDominantGenome();
        this.calculateAverageEnergy();
        this.calculateAverageLifeSpan();
        this.calculateAverageDescendantAmount();
    }

    public SimulationStats(WorldMap map, String file){
        this(map);
        saveDayToCsv(file);
    }

    private void saveDayToCsv(String file){
        try (FileWriter writer = new FileWriter(file, true)){
            if (day == 0){
                writer.write("Day,Live Animals,Dead Animals,Grass Amount,Dominant Genome,Average Energy,Average Life Span,Average Descendant Amount\n");
            }
            writer.write(String.format("%d,%d,%d,%d,%s,%.2f,%.2f,%.2f\n",
                    day,
                    liveAnimalsAmount,
                    deadAnimalsAmount,
                    grassAmount,
                    dominantGenome != null ? dominantGenome.toString() : "None",
                    averageEnergy,
                    averageLifeSpan,
                    averageDescendantAmount
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void calculateDominantGenome() {
        Map<Genome, Integer> genomeCounter = new HashMap<>();
        for (PriorityQueue<Animal> animals : liveAnimals.values()) {
            for (Animal animal : animals) {
                genomeCounter.put(animal.getGenotype(), genomeCounter.getOrDefault(animal.getGenotype(), 0) + 1);
            }
        }
        for (PriorityQueue<Animal> animals : deadAnimals.values()) {
            for (Animal animal : animals) {
                genomeCounter.put(animal.getGenotype(), genomeCounter.getOrDefault(animal.getGenotype(), 0) + 1);
            }
        }
        this.dominantGenome = genomeCounter.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    private void calculateAverageEnergy() {
        this.averageEnergy = (double) liveAnimals.values().stream().mapToInt(animals -> animals.stream().mapToInt(Animal::getEnergy).sum()).sum() / (liveAnimalsAmount != 0 ? liveAnimalsAmount : 1);
    }

    private void calculateAverageLifeSpan() {
        this.averageLifeSpan = (double) deadAnimals.values().stream().mapToInt(animals -> animals.stream().mapToInt(animal -> animal.getDeathDate() - animal.getBirthDate()).sum()).sum() / (deadAnimalsAmount != 0 ? deadAnimalsAmount : 1);
    }

    private void calculateAverageDescendantAmount() {
        this.averageDescendantAmount = (double) liveAnimals.values().stream().mapToInt(animals -> animals.stream().mapToInt(Animal::getDescendantsCounter).sum()).sum() / (liveAnimalsAmount != 0 ? liveAnimalsAmount : 1);
    }

    public int getDay() {
        return day;
    }

    public int getLiveAnimalsAmount() {
        return liveAnimalsAmount;
    }
    public int getDeadAnimalsAmount() {
        return deadAnimalsAmount;
    }

    public int getGrassAmount() {
        return grassAmount;
    }

    public Genome getDominantGenome() {
        return dominantGenome;
    }

    public double getAverageEnergy() {
        return averageEnergy;
    }

    public double getAverageLifeSpan() {
        return averageLifeSpan;
    }

    public double getAverageDescendantAmount() {
        return averageDescendantAmount;
    }
}
