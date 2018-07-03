package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.ConsolidateGridViews
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 4/2/15
 * Time: 6:44 PM
 */
@Component
class TBGameMasker extends AbstractMultiPlayerGameMasker<ObjectId, GameFeature, TBGame, TBMaskedGame> {
    @Autowired
    ConsolidateGridViews consolidateGridViews

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
            final TBGame mpGame,
            final TBMaskedGame playerMaskedGame) {
        super.copyUnmaskedData(mpGame, playerMaskedGame)
        TBMaskedGame masked = (TBMaskedGame) playerMaskedGame
        TBGame game = (TBGame) mpGame
        masked.remainingMoves = game.remainingMoves
        masked.movesForSpecials = game.movesForSpecials
        masked.gridSize = game.gridSize
        masked.startingShips = game.startingShips
    }

    @Override
    protected void copyMaskedData(
            final TBGame mpGame,
            final Player<ObjectId> player,
            final TBMaskedGame playerMaskedGame,
            final Map<ObjectId, Player<ObjectId>> idMap) {
        super.copyMaskedData(mpGame, player, playerMaskedGame, idMap)
        TBMaskedGame masked = (TBMaskedGame) playerMaskedGame
        TBGame game = (TBGame) mpGame
        masked.maskedPlayersState = createMaskedPlayerState(game.playerDetails[player.id], idMap)
        masked.maskedPlayersState.consolidatedOpponentView = consolidateGridViews.createConsolidatedView(
                game,
                masked.maskedPlayersState.opponentViews.values().toList()
        )
        masked.currentPlayer = idMap[game.currentPlayer].md5
        int maxScore = -1;
        String winningPlayer;
        game.playerDetails.each {
            ObjectId playerId, TBPlayerState state ->

                String md5 = idMap[playerId].md5
                masked.playersAlive[md5] = state.alive
                masked.playersScore[md5] = state.totalScore
                masked.playersSetup[md5] = state.setup
                if (state.totalScore > maxScore) {
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
        maskedPlayerState.cruiseMissilesRemaining = playerState.cruiseMissilesRemaining
        maskedPlayerState.startingShips = playerState.startingShips
        maskedPlayerState.actionLog = playerState.actionLog.collect {
            new TBMaskedActionLogEntry(
                    convertTime(it.timestamp),
                    it.actionType,
                    it.description
            )
        }

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
}
