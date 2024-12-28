package org.example.model;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    @Override
    public String toString() {
        return switch (this) {
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case EAST -> "E";
            case SOUTH_EAST -> "SE";
            case SOUTH -> "S";
            case SOUTH_WEST -> "SW";
            case WEST -> "W";
            case NORTH_WEST -> "NW";
        };
    }

    public Vector2d toUnitVector() {
        return switch (this){
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_WEST -> new Vector2d(-1, 1);
        };
    }

    public MapDirection next() {
        return switch (this){
            case NORTH -> MapDirection.NORTH_EAST;
            case NORTH_EAST -> MapDirection.EAST;
            case EAST -> MapDirection.SOUTH_EAST;
            case SOUTH_EAST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.SOUTH_WEST;
            case SOUTH_WEST -> MapDirection.WEST;
            case WEST -> MapDirection.NORTH_WEST;
            case NORTH_WEST -> MapDirection.NORTH;
        };
    }

    public MapDirection rotate(int angle) {
        MapDirection result = this;
        for (int i = 0; i < angle; i++) {
            result = result.next();
        }
        return result;
    }
}
