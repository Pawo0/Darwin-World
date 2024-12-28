package org.example.model;

import java.util.Vector;

public class Grass implements WorldElement{

    Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }
}
