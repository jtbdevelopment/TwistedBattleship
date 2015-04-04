package com.jtbdevelopment.TwistedBattleship.state.ships

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:53 AM
 */
@CompileStatic
enum Ship {
    Carrier(5, 'Aircraft Carrier'),
    Battleship(4, 'Battleship'),
    Cruiser(3, 'Cruiser'),
    Submarine(3, 'Submarine'),
    Destroyer(2, 'Destroyer')

    final int gridSize
    final String description

    private Ship(final int gridSize, final String description) {
        this.gridSize = gridSize
        this.description = description
    }
}