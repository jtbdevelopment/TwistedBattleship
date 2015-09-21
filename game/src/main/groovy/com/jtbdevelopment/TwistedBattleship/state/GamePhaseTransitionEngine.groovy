package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
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
            game.playerDetails.each {
                ObjectId myId, TBPlayerState myState ->
                    myState.opponentGrids.each {
                        ObjectId opponent, Grid myGrid ->
                            TBPlayerState theirState = game.playerDetails[opponent]
                            Grid theirGrid = theirState.opponentViews[myId]
                            theirState.shipStates.each {
                                ShipState shipState ->
                                    for (int i = 0; i < shipState.ship.gridSize; ++i) {
                                        GridCellState cell = shipState.shipSegmentHit[i] ? GridCellState.HiddenHit : GridCellState.RevealedShip
                                        def coordinate = shipState.shipGridCells[i]
                                        if (myGrid.get(coordinate).rank < cell.rank) {
                                            myGrid.set(coordinate, cell)
                                            theirGrid.set(coordinate, cell)
                                        }
                                    }
                            }
                    }
            }
            return changeStateAndReevaluate(GamePhase.RoundOver, game)
        }
        return game;
    }
}
