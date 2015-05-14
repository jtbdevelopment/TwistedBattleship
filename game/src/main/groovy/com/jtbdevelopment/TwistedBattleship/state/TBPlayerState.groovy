package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 4/2/15
 * Time: 6:40 AM
 */
@CompileStatic
class TBPlayerState implements Serializable {
    private static int SHIP_COUNT = Ship.values().size()

    Map<Ship, ShipState> shipStates = [:]

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
        return shipStates.size() == SHIP_COUNT
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
        return shipStates.values().findAll { ShipState it -> it.healthRemaining > 0 }.size()
    }

    void setActiveShipsRemaining(final int remaining) {
        //  ignore
    }
}
