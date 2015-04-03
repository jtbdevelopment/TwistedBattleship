package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:40 AM
 */
@CompileStatic
class TBPlayerState {
    Map<Ships, Integer> shipHealthRemaining = [:]
    Map<Ships, List<GridCoordinate>> shipPositions = [:]
    int activeShipsRemaining = 0

    int spysRemaining = 0
    int evasiveManeuversRemaining = 0
    int ecmsRemaining = 0
    int emergencyRepairsRemaining = 0

    int scoreFromHits = 0
    int scoreFromSinks = 0
    int scoreFromLiving = 0

    Grid playerGrid
    Map<String, Grid> opponentGrids = [:]  // md5 key

    int getTotalScore() {
        scoreFromHits + scoreFromSinks + scoreFromLiving
    }
}
