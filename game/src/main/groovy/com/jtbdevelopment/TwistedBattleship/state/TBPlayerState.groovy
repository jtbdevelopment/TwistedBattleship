package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Transient

/**
 * Date: 4/2/15
 * Time: 6:40 AM
 */
@CompileStatic
class TBPlayerState implements Serializable {
    List<Ship> startingShips = []
    List<ShipState> shipStates = []

    @Transient
    Map<GridCoordinate, ShipState> coordinateShipMap = null

    int spysRemaining = 0
    int evasiveManeuversRemaining = 0
    int ecmsRemaining = 0
    int emergencyRepairsRemaining = 0

    int scoreFromHits = 0
    int scoreFromSinks = 0
    int scoreFromLiving = 0

    Map<ObjectId, Grid> opponentGrids = [:]
    Map<ObjectId, Grid> opponentViews = [:]

    String lastActionMessage = ""

    void setStartingShips(final List<Ship> startingShips) {
        this.startingShips = startingShips
        this.startingShips.sort()
    }

    int getTotalScore() {
        scoreFromHits + scoreFromSinks + scoreFromLiving
    }

    void setTotalScore(final int totalScore) {
        //  ignore
    }

    boolean getSetup() {
        return isSetup()
    }

    boolean isSetup() {
        return startingShips.size() > 0 &&
                shipStates.size() == startingShips.size() &&
                shipStates.collect { it.ship } == startingShips
    }

    void setSetup(final boolean isSetup) {
        // ignore
    }

    boolean getAlive() {
        return isAlive()
    }

    boolean isAlive() {
        return activeShipsRemaining > 0
    }

    void setAlive(final boolean alive) {
        // ignore
    }

    int getActiveShipsRemaining() {
        return shipStates.findAll { ShipState it -> it.healthRemaining > 0 }.size()
    }

    void setActiveShipsRemaining(final int remaining) {
        //  ignore
    }

    List<ShipState> getShipStates() {
        return shipStates
    }

    void setShipStates(final List<ShipState> shipStates) {
        this.shipStates = shipStates
        this.shipStates.sort(new Comparator<ShipState>() {
            @Override
            int compare(final ShipState o1, final ShipState o2) {
                return o1.ship.compareTo(o2.ship)
            }
        })
        computeCoordinateShipMap()
    }

    Map<GridCoordinate, ShipState> getCoordinateShipMap() {
        if (coordinateShipMap == null || coordinateShipMap.isEmpty()) {
            computeCoordinateShipMap()
        }
        return coordinateShipMap
    }

    void setCoordinateShipMap(final Map<GridCoordinate, ShipState> coordinateShipMap) {
        //  ignore
    }

    private void computeCoordinateShipMap() {
        def temp = (Map<GridCoordinate, ShipState>) shipStates.collectEntries {
            ShipState state ->
                state.shipGridCells.collectEntries {
                    GridCoordinate coordinate ->
                        [(coordinate): state]
                }
        }
        this.coordinateShipMap = temp
    }

}
