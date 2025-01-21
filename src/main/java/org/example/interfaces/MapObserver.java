package org.example.interfaces;

public interface MapObserver {
    void addObserver(MapChangeListener observer);

    void notifyObservers(String message);
}
