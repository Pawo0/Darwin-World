package org.example.simulations;

import org.example.genomes.Genome;
import org.example.genomes.GenomeSwap;
import org.example.map.WorldMap;
import org.example.map.objects.Animal;
import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation implements Runnable {

    private final WorldMap map;
    private final List<Animal> animals;
    private final SimulationSettings settings;

//    " Without volatile, readers could see some non-updated value." - some smart guy on stackoverflow
    private volatile boolean running = true;
    private volatile boolean paused = false;

    public Simulation(SimulationSettings settings, WorldMap map) {
        this.map = map;
//        to see in console
//        this.map.addObserver(new ConsoleMapDisplay());
        this.settings = settings;

        Random random = new Random();
        this.animals = new ArrayList<>();
        for (int i = 0; i < settings.getStartAmountOfAnimals(); i++) {
            int x = random.nextInt(settings.getMapWidth());
            int y = random.nextInt(settings.getMapHeight());
            Genome genome = switch (settings.isSpecialMutation()) {
                case SWAP -> new GenomeSwap(settings);
                case DEFAULT -> new Genome(settings);
            };
            animals.add(new Animal(genome, new Vector2d(x, y), settings, 0));
        }

        for (Animal animal : animals) {
            map.place(animal);
        }
    }

    @Override
    public void run() {
        int day = 0;

        while (running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }


            map.nextDay();
            day++;
            System.out.println("Day: " + day);

            try {
                Thread.sleep(settings.getRefreshTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (map.liveAnimalsAmount() == 0) {
                System.out.println("All animals are dead. Simulation ending.");
                running = false;
            }
        }
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll(); // powiadamia zatrzymane watki zeby wystartowaly (zatrzymane przez "wait" w srodku run)
    }

    public void stop() {
        running = false;
        resume(); // trzeba wzowic zeby przerwac xd
    }

    public boolean isRunning() {
        return running;
    }
}
