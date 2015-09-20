package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:36 PM
 */
@CompileStatic
class TBMaskedGame extends AbstractMaskedMultiPlayerGame<GameFeature> {
    Map<String, Boolean> playersSetup = [:]
    Map<String, Boolean> playersAlive = [:]
    Map<String, Integer> playersScore = [:]
    List<Ship> startingShips = []
    TBMaskedPlayerState maskedPlayersState
    String generalMessage
    String currentPlayer
    String winningPlayer
    //  TODO support non-symmetrical
    int gridSize
    int remainingMoves
    int movesForSpecials
}
