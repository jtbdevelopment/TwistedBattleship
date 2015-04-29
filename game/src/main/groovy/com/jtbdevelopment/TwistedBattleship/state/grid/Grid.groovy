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

    @SuppressWarnings("unused") // deserializer
    private Grid() {
    }

    public Grid(final int size) {
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
}
