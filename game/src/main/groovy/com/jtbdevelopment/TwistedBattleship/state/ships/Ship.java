package com.jtbdevelopment.TwistedBattleship.state.ships;

/**
 * Date: 4/2/15
 * Time: 6:53 AM
 */
public enum Ship {
    Carrier(5, "Aircraft Carrier"),
    Battleship(4, "Battleship"),
    Cruiser(3, "Cruiser"),
    Submarine(3, "Submarine"),
    Destroyer(2, "Destroyer");

    private final int gridSize;
    private final String description;

    Ship(final int gridSize, final String description) {
        this.gridSize = gridSize;
        this.description = description;
    }

    public final int getGridSize() {
        return gridSize;
    }

    public final String getDescription() {
        return description;
    }
}
