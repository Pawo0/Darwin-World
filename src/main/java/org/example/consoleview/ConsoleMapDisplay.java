package org.example.consoleview;

import org.example.interfaces.MapChangeListener;
import org.example.map.WorldMap;

public class ConsoleMapDisplay implements MapChangeListener {
    private int updateCount = 0;

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        synchronized (System.out){
            System.out.println("On map: " + worldMap.getId());
            System.out.println("Update #" + (++updateCount) + ": " + message);
            System.out.println(worldMap);
        }
    }
}