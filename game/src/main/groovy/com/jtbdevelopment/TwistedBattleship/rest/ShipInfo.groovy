package com.jtbdevelopment.TwistedBattleship.rest

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

/**
 * Date: 7/1/15
 * Time: 6:36 PM
 */
class ShipInfo {
    Ship ship
    String description
    int gridSize

    @SuppressWarnings("GroovyUnusedDeclaration")
    ShipInfo() {
    }

    ShipInfo(final Ship ship) {
        this.ship = ship
        this.description = ship.description
        this.gridSize = ship.gridSize
    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final ShipInfo shipInfo = (ShipInfo) o

        if (gridSize != shipInfo.gridSize) return false
        if (description != shipInfo.description) return false
        if (ship != shipInfo.ship) return false

        return true
    }

    int hashCode() {
        int result
        result = ship.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + gridSize
        return result
    }
}
