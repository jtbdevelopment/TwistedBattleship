package com.jtbdevelopment.TwistedBattleship.rest;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;

/**
 * Date: 5/12/15
 * Time: 6:36 AM
 */
public class Target {
    private String player;
    private GridCoordinate coordinate;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public GridCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(GridCoordinate coordinate) {
        this.coordinate = coordinate;
    }
}
