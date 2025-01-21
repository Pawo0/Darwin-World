package org.example.map.objects;

import org.example.interfaces.WorldElement;
import org.example.model.Vector2d;

public record Grass(Vector2d position) implements WorldElement {

    @Override
    public String toString() {
        return "*";
    }
}
