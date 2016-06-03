package com.jtbdevelopment.TwistedBattleship.state.ships

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import groovy.transform.CompileStatic
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient

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
    @Transient
    boolean horizontal

    @SuppressWarnings("unused")
    protected ShipState() {
    }

    //  TODO - why is this sortedSet?
    ShipState(final Ship ship,
              final SortedSet<GridCoordinate> shipGridCells) {
        this.ship = ship
        this.shipGridCells = (shipGridCells as List).sort()
        this.healthRemaining = ship.gridSize
        this.shipSegmentHit = shipGridCells.collect { false }
        computeHorizontal()
    }

    @PersistenceConstructor
    ShipState(
            final Ship ship,
            final int healthRemaining,
            final List<GridCoordinate> shipGridCells,
            final List<Boolean> shipSegmentHit) {
        this.ship = ship
        this.healthRemaining = healthRemaining
        this.shipGridCells = shipGridCells.sort()
        this.shipSegmentHit = shipSegmentHit
        computeHorizontal()
    }

    void setShipGridCells(final List<GridCoordinate> shipGridCells) {
        this.shipGridCells = shipGridCells
        computeHorizontal()
    }

    void setHorizontal(final boolean horizontal) {
        //  ignore
    }

    private void computeHorizontal() {
        this.horizontal = this.shipGridCells.size() >= 2 && this.shipGridCells[0].row == this.shipGridCells[1].row;
    }
}
