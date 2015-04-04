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
class TBPlayerState {
    Map<Ship, ShipState> shipStates = [:]
    int activeShipsRemaining = 0

    int spysRemaining = 0
    int evasiveManeuversRemaining = 0
    int ecmsRemaining = 0
    int emergencyRepairsRemaining = 0

    int scoreFromHits = 0
    int scoreFromSinks = 0
    int scoreFromLiving = 0

    Map<ObjectId, Grid> opponentGrids = [:]
    Map<ObjectId, Grid> opponentViews = [:]

    int getTotalScore() {
        scoreFromHits + scoreFromSinks + scoreFromLiving
    }
}
