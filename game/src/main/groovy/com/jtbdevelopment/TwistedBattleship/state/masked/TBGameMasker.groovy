package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

import java.time.ZonedDateTime

/**
 * Date: 4/2/15
 * Time: 6:44 PM
 */
@CompileStatic
@Component
class TBGameMasker extends AbstractMultiPlayerGameMasker<ObjectId, GameFeature, TBGame, TBMaskedGame> {
    @Override
    protected TBMaskedGame newMaskedGame() {
        return new TBMaskedGame()
    }

    @Override
    Class<ObjectId> getIDClass() {
        return ObjectId.class
    }

    @Override
    protected void copyUnmaskedData(
            final MultiPlayerGame<ObjectId, ZonedDateTime, GameFeature> mpGame,
            final MaskedMultiPlayerGame<GameFeature> playerMaskedGame) {
        super.copyUnmaskedData(mpGame, playerMaskedGame)
        TBMaskedGame masked = (TBMaskedGame) playerMaskedGame
        TBGame game = (TBGame) mpGame
        masked.generalMessage = game.generalMessage
        masked.remainingMoves = game.remainingMoves
        masked.movesForSpecials = game.movesForSpecials
        masked.gridSize = game.gridSize
    }

    @Override
    protected void copyMaskedData(
            final MultiPlayerGame<ObjectId, ZonedDateTime, GameFeature> mpGame,
            final Player<ObjectId> player,
            final MaskedMultiPlayerGame<GameFeature> playerMaskedGame,
            final Map<ObjectId, Player<ObjectId>> idMap) {
        super.copyMaskedData(mpGame, player, playerMaskedGame, idMap)
        TBMaskedGame masked = (TBMaskedGame) playerMaskedGame
        TBGame game = (TBGame) mpGame
        masked.maskedPlayersState = createMaskedPlayerState(game.playerDetails[player.id], idMap)
        masked.maskedPlayersState.consolidatedOpponentView = createConsolidatedView(game, masked)
        masked.currentPlayer = idMap[game.currentPlayer].md5
        int maxScore = -1;
        String winningPlayer;
        game.playerDetails.each {
            ObjectId playerId, TBPlayerState state ->

                String md5 = idMap[playerId].md5
                masked.playersAlive[md5] = state.alive
                masked.playersScore[md5] = state.totalScore
                masked.playersSetup[md5] = state.setup
                if(state.totalScore > maxScore) {
                    winningPlayer = md5
                    maxScore = state.totalScore
                }
        }
        masked.winningPlayer = winningPlayer
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    TBMaskedPlayerState createMaskedPlayerState(final TBPlayerState playerState,
                                                final Map<ObjectId, Player<ObjectId>> idMap) {
        TBMaskedPlayerState maskedPlayerState = new TBMaskedPlayerState()

        maskedPlayerState.shipStates = playerState.shipStates
        maskedPlayerState.activeShipsRemaining = playerState.activeShipsRemaining

        maskedPlayerState.totalScore = playerState.totalScore
        maskedPlayerState.alive = playerState.alive
        maskedPlayerState.setup = playerState.setup
        maskedPlayerState.totalScore = playerState.totalScore
        maskedPlayerState.ecmsRemaining = playerState.ecmsRemaining
        maskedPlayerState.emergencyRepairsRemaining = playerState.emergencyRepairsRemaining
        maskedPlayerState.evasiveManeuversRemaining = playerState.evasiveManeuversRemaining
        maskedPlayerState.spysRemaining = playerState.spysRemaining
        maskedPlayerState.lastActionMessage = playerState.lastActionMessage

        maskedPlayerState.opponentViews = (Map<String, Grid>) playerState.opponentViews.collectEntries {
            ObjectId id, Grid view ->
                [(idMap[id].md5): view]
        }
        maskedPlayerState.opponentGrids = (Map<String, Grid>) playerState.opponentGrids.collectEntries {
            ObjectId id, Grid view ->
                [(idMap[id].md5): view]
        }
        return maskedPlayerState
    }

    Grid createConsolidatedView(final TBGame game, final TBMaskedGame masked) {
        int size = game.gridSize
        Grid consolidatedView = new Grid(size)
        (0..size - 1).each {
            int row ->
                (0..size - 1).each {
                    int col ->
                        List<GridCellState> states = masked.maskedPlayersState.opponentViews.collect {
                            it.value.get(row, col)
                        }.sort {
                            GridCellState a, GridCellState b ->
                                b.rank - a.rank // reverse sort
                        }
                        consolidatedView.set(
                                row,
                                col,
                                states[0])
                }
        }
        consolidatedView
    }
}
