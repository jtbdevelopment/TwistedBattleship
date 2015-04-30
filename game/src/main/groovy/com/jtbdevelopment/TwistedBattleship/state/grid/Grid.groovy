package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 7:07 AM
 */
@CompileStatic
class Grid implements Serializable {

    final GridCellState[][] table;
    final int size;

    // json deserializer
    @SuppressWarnings("unused")
    protected Grid() {
        size = 0;
        table = new GridCellState[0][0]
    }

    Grid(final int size) {
        this.size = size;
        table = new GridCellState[size][size]
        (1..size).each {
            int row ->
                (1..size).each {
                    int col ->
                        table[row - 1][col - 1] = GridCellState.Unknown
                }
        }
    }

    GridCellState get(final int row, final int column) {
        return table[row][column]
    }

    Grid set(final int row, final int column, final GridCellState state) {
        table[row][column] = state
        return this
    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (!(o instanceof Grid)) return false

        final Grid grid = (Grid) o

        if (size != grid.size) return false
        for (int i = 0; i < grid.size; ++i) {
            for (int j = 0; j < grid.size; ++j) {
                if (this.table[i][j] != grid.table[i][j]) return false
            }
        }

        return true
    }

    int hashCode() {
        return size
    }
}
