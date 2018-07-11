package com.jtbdevelopment.TwistedBattleship.state.ships;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date: 4/3/15
 * Time: 7:35 PM
 */
public class ShipState implements Serializable {
    @SuppressWarnings("unused")
    protected ShipState() {
    }

    private Ship ship;
    private int healthRemaining;
    private List<GridCoordinate> shipGridCells;
    private List<Boolean> shipSegmentHit;
    @Transient
    private boolean horizontal;

    public ShipState(final Ship ship, final Set<GridCoordinate> shipGridCells) {
        this.ship = ship;
        this.shipGridCells = new ArrayList<>(shipGridCells);
        Collections.sort(this.shipGridCells);
        this.healthRemaining = ship.getGridSize();
        this.shipSegmentHit = shipGridCells.stream().map(x -> false).collect(Collectors.toList());
        computeHorizontal();
    }

    @PersistenceConstructor
    public ShipState(final Ship ship, final int healthRemaining, final List<GridCoordinate> shipGridCells, final List<Boolean> shipSegmentHit) {
        this.ship = ship;
        this.healthRemaining = healthRemaining;
        this.shipGridCells = new ArrayList<>(shipGridCells);
        Collections.sort(this.shipGridCells);
        this.shipSegmentHit = shipSegmentHit;
        computeHorizontal();
    }

    private void computeHorizontal() {
        this.horizontal = this.shipGridCells.size() >= 2 && this.shipGridCells.get(0).getRow() == this.shipGridCells.get(1).getRow();
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public int getHealthRemaining() {
        return healthRemaining;
    }

    public void setHealthRemaining(int healthRemaining) {
        this.healthRemaining = healthRemaining;
    }

    public List<GridCoordinate> getShipGridCells() {
        return shipGridCells;
    }

    public void setShipGridCells(final List<GridCoordinate> shipGridCells) {
        this.shipGridCells = shipGridCells;
        computeHorizontal();
    }

    public List<Boolean> getShipSegmentHit() {
        return shipSegmentHit;
    }

    public void setShipSegmentHit(List<Boolean> shipSegmentHit) {
        this.shipSegmentHit = shipSegmentHit;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    @SuppressWarnings("WeakerAccess")
    public void setHorizontal(@SuppressWarnings("unused") final boolean horizontal) {
        //  ignore
    }
}
