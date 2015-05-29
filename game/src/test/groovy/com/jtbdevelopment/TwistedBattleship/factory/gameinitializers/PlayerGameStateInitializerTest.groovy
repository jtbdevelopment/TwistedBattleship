package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import org.bson.types.ObjectId

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class PlayerGameStateInitializerTest extends MongoGameCoreTestCase {
    PlayerGameStateInitializer initializer = new PlayerGameStateInitializer()

    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.CriticalEnabled, GameFeature.ECMEnabled, GameFeature.CriticalEnabled.EREnabled, GameFeature.SpyEnabled, GameFeature.CriticalEnabled.EMEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        assert [:] == game.playerDetails
        initializer.initializeGame(game)
        assert 15 == game.gridSize
        assert 3 == game.playerDetails.size()
        assert PONE.id == game.currentPlayer
        assert 1 == game.remainingMoves
        game.players.each {
            Player p ->
                def expectedSpecials = 2
                validatePlayerStates(game, p, expectedSpecials)
        }
    }

    void testInitializeGameGrid10x10() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid10x10, GameFeature.Single, GameFeature.CriticalEnabled, GameFeature.ECMEnabled, GameFeature.CriticalEnabled.EREnabled, GameFeature.SpyEnabled, GameFeature.CriticalEnabled.EMEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        assert [:] == game.playerDetails
        initializer.initializeGame(game)
        assert 10 == game.gridSize
        assert 3 == game.playerDetails.size()
        assert PONE.id == game.currentPlayer
        assert 1 == game.remainingMoves
        game.players.each {
            Player p ->
                def expectedSpecials = 2
                validatePlayerStates(game, p, expectedSpecials)
        }
    }

    void testInitializeGameNoOptionalFeatures() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid20x20],
                players: [PTWO, PTHREE, PONE]
        )

        assert game.playerDetails == [:]
        initializer.initializeGame(game)
        assert 20 == game.gridSize
        assert 3 == game.playerDetails.size()
        assert PTWO.id == game.currentPlayer
        assert Ship.values().size() == game.remainingMoves
        game.players.each {
            Player p ->
                validatePlayerStates(game, p, 0)
        }
    }

    protected void validatePlayerStates(TBGame game, Player p, int expectedSpecials) {
        Set<ObjectId> opponentIds = game.players.collect { it.id } as Set
        opponentIds.remove(p.id)
        TBPlayerState playerState = game.playerDetails[p.id]
        assert playerState
        assert expectedSpecials == playerState.ecmsRemaining
        assert expectedSpecials == playerState.evasiveManeuversRemaining
        assert expectedSpecials == playerState.emergencyRepairsRemaining
        assert expectedSpecials == playerState.spysRemaining
        assert opponentIds == playerState.opponentGrids.keySet()
        playerState.opponentGrids.values().each {
            Grid it ->
                it.size == game.gridSize
                it.table.each {
                    GridCellState[] row ->
                        row.each {
                            GridCellState state ->
                                assert state == GridCellState.Unknown
                        }
                }
        }
        assert opponentIds == playerState.opponentViews.keySet()
        playerState.opponentViews.values().each {
            Grid it ->
                it.size == game.gridSize
                it.table.each {
                    GridCellState[] row ->
                        row.each {
                            GridCellState state ->
                                assert state == GridCellState.Unknown
                        }
                }
        }
    }

}
