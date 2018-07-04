package com.jtbdevelopment.TwistedBattleship.state.grid;

import java.io.Serializable;

/**
 * Date: 4/2/15
 * Time: 7:07 AM
 */
public class Grid implements Serializable {
    private final GridCellState[][] table;
    private final int size;

    @SuppressWarnings("unused")
    protected Grid() {
        size = 0;
        table = new GridCellState[0][0];
    }

    public Grid(final int size) {
        this.size = size;
        table = new GridCellState[size][size];
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                table[row][col] = GridCellState.Unknown;
            }
        }
    }

    public GridCellState get(final GridCoordinate coordinate) {
        return get(coordinate.getRow(), coordinate.getColumn());
    }

    public GridCellState get(final int row, final int column) {
        return table[row][column];
    }

    public Grid set(final GridCoordinate coordinate, final GridCellState state) {
        return set(coordinate.getRow(), coordinate.getColumn(), state);
    }

    public Grid set(final int row, final int column, final GridCellState state) {
        table[row][column] = state;
        return this;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Grid)) return false;

        final Grid grid = (Grid) o;

        if (size != grid.getSize()) return false;
        for (int i = 0; i < grid.getSize(); i = ++i) {
            for (int j = 0; j < grid.getSize(); j = ++j) {
                if (!this.table[i][j].equals(grid.getTable()[i][j])) return false;
            }

        }


        return true;
    }

    public int hashCode() {
        return size;
    }

    public final GridCellState[][] getTable() {
        return table;
    }

    public final int getSize() {
        return size;
    }
}
