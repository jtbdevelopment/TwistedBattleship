package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.transition.AbstractGamePhaseTransitionEngine
import org.bson.types.ObjectId
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
        game.generalMessage = "Begin!"
        return changeStateAndReevaluate(GamePhase.Playing, game)
    }

    @Override
    protected TBGame evaluatePlayingPhase(final TBGame game) {
        Map<ObjectId, TBPlayerState> alivePlayers = game.playerDetails.findAll() {
            it.value.alive
        }
        if (alivePlayers.size() == 1) {
            String message = game.players.find {
                it.id == alivePlayers.keySet().iterator().next()
            }.displayName + " defeated all challengers!"
            gameScorer.scoreGame(game)
            game.playerDetails.each { it.value.lastActionMessage = message }
            return changeStateAndReevaluate(GamePhase.RoundOver, game)
        }
        return game;
    }
}
