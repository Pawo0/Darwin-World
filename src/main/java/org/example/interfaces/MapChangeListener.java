package org.example.interfaces;

import org.example.model.WorldMap;

public interface MapChangeListener {
    void mapChanged(WorldMap worldMap, String message);
}
