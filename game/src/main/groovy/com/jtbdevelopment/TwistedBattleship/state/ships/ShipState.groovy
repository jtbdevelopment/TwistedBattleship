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
    SortedSet<GridCoordinate> shipGridCells // from front to back
    List<Boolean> shipSegmentHit

    @SuppressWarnings("unused")
    protected ShipState() {
    }

    ShipState(final Ship ship,
              final SortedSet<GridCoordinate> shipGridCells) {
        this.ship = ship
        this.shipGridCells = shipGridCells
        this.healthRemaining = ship.gridSize
        this.shipSegmentHit = shipGridCells.collect { false }
    }

    @PersistenceConstructor
    ShipState(
            final Ship ship,
            final int healthRemaining,
            final SortedSet<GridCoordinate> shipGridCells,
            final List<Boolean> shipSegmentHit) {
        this.ship = ship
        this.healthRemaining = healthRemaining
        this.shipGridCells = shipGridCells
        this.shipSegmentHit = shipSegmentHit
    }

}
