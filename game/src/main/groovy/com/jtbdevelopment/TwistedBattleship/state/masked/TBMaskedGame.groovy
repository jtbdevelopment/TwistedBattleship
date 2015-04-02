package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeatures
import com.jtbdevelopment.TwistedBattleship.state.GamePhase
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:36 PM
 */
@CompileStatic
class TBMaskedGame extends AbstractMaskedMultiPlayerGame<GameFeatures> {
    Long rematchTimestamp

    GamePhase gamePhase

    Map<String, Boolean> playersAlive = [:]
    Map<String, Integer> playersScore = [:]
    TBPlayerState maskedPlayersState
}
