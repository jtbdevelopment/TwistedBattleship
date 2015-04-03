package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.GamePhase
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:36 PM
 */
@CompileStatic
class TBMaskedGame extends AbstractMaskedMultiPlayerGame<GameFeature> {
    Long rematchTimestamp

    GamePhase gamePhase

    Map<String, Boolean> playersAlive = [:]
    Map<String, Integer> playersScore = [:]
    TBPlayerState maskedPlayersState
}
