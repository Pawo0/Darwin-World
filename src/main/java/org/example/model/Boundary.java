package org.example.model;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {
    public boolean contains(Vector2d position) {
        return position.precedes(lowerLeft) && position.follows(upperRight);
    }
}
