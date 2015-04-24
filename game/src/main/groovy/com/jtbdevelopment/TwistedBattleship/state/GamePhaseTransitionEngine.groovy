package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.transition.AbstractGamePhaseTransitionEngine
import org.springframework.stereotype.Component

/**
 * Date: 4/22/15
 * Time: 9:07 PM
 */
@Component
class GamePhaseTransitionEngine extends AbstractGamePhaseTransitionEngine<TBGame> {
    @Override
    protected TBGame evaluateSetupPhase(final TBGame game) {
        if (game.playerDetails.values().find {
            TBPlayerState playerState ->
                !playerState.setup
        } != null) {
            return game
        }
        return changeStateAndReevaluate(GamePhase.Playing, game)
    }

    @Override
    protected TBGame evaluatePlayingPhase(final TBGame game) {
        if (game.playerDetails.values().findAll() {
            TBPlayerState playerState ->
                playerState.alive
        }.size() == 1) {
            return changeStateAndReevaluate(GamePhase.RoundOver, game)
        }
        return game;
    }
}
