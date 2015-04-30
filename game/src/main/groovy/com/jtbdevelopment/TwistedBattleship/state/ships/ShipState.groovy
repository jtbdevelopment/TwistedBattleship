package com.jtbdevelopment.TwistedBattleship.state.ships

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import groovy.transform.CompileStatic
import org.springframework.data.annotation.PersistenceConstructor

/**
 * Date: 4/3/15
 * Time: 7:35 PM
 */
@CompileStatic
class ShipState implements Serializable {
    Ship ship
    int healthRemaining
    List<GridCoordinate> shipGridCells // from front to back
    List<Boolean> shipSegmentHit

    ShipState(final Ship ship,
              final List<GridCoordinate> shipGridCells) {
        this.ship = ship
        this.shipGridCells = shipGridCells
        this.healthRemaining = ship.gridSize
        this.shipSegmentHit = shipGridCells.collect { false }
    }

    @PersistenceConstructor
    ShipState(
            final Ship ship,
            final int healthRemaining,
            final List<GridCoordinate> shipGridCells,
            final List<Boolean> shipSegmentHit) {
        this.ship = ship
        this.healthRemaining = healthRemaining
        this.shipGridCells = shipGridCells
        this.shipSegmentHit = shipSegmentHit
    }
}
