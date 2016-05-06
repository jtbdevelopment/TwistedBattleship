package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import groovy.transform.CompileStatic

/**
 * Date: 4/30/15
 * Time: 6:59 AM
 */
@CompileStatic
class TBMaskedPlayerState {
    boolean setup
    boolean alive

    List<TBMaskedActionLogEntry> actionLog = []

    List<Ship> startingShips = []
    List<ShipState> shipStates = []
    int activeShipsRemaining

    int spysRemaining = 0
    int cruiseMissilesRemaining = 0
    int evasiveManeuversRemaining = 0
    int ecmsRemaining = 0
    int emergencyRepairsRemaining = 0

    int totalScore

    Map<String, Grid> opponentGrids = [:]
    Map<String, Grid> opponentViews = [:]
    Grid consolidatedOpponentView
}
