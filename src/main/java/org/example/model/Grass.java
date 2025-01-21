package org.example.model;

import org.example.interfaces.WorldElement;

public record Grass(Vector2d position) implements WorldElement {

    @Override
    public String toString() {
        return "*";
    }
}
