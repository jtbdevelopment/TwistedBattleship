package com.jtbdevelopment.TwistedBattleship.rest.services.messages;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;

import java.util.List;

/**
 * Date: 9/20/2015
 * Time: 5:27 PM
 */
public class ShipAndCoordinates {
    private Ship ship;
    private List<GridCoordinate> coordinates;

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public List<GridCoordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<GridCoordinate> coordinates) {
        this.coordinates = coordinates;
    }
}
