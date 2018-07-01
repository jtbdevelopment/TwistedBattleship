package com.jtbdevelopment.TwistedBattleship.rest;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

/**
 * Date: 7/1/15
 * Time: 6:36 PM
 */
public class ShipInfo {
    private Ship ship;
    private String description;
    private int gridSize;

    @SuppressWarnings("unused")
    public ShipInfo() {
    }

    public ShipInfo(final Ship ship) {
        this.ship = ship;
        this.description = ship.getDescription();
        this.gridSize = ship.getGridSize();
    }

    public boolean equals(final Object o) {
        if (DefaultGroovyMethods.is(this, o)) return true;
        if (!getClass().equals(o.getClass())) return false;

        final ShipInfo shipInfo = (ShipInfo) o;

        if (gridSize != shipInfo.getGridSize()) return false;
        if (!description.equals(shipInfo.getDescription())) return false;
        return ship.equals(shipInfo.getShip());
    }

    public int hashCode() {
        int result;
        result = ship.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + gridSize;
        return result;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
