package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import org.bson.types.ObjectId

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class PlayerStateInitializerTest extends MongoGameCoreTestCase {
    PlayerStateInitializer initializer = new PlayerStateInitializer()

    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.CriticalEnabled, GameFeature.ECMEnabled, GameFeature.CriticalEnabled.EREnabled, GameFeature.SpyEnabled, GameFeature.CriticalEnabled.EMEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        int size = 11
        initializer.util = [
                getSize: {
                    GameFeature feature ->
                        assert feature == GameFeature.Grid15x15
                        return size
                }
        ] as GridSizeUtil
        assert game.playerDetails == [:]
        initializer.initializeGame(game)
        assert game.playerDetails.size() == 3
        game.players.each {
            Player p ->
                def expectedSpecials = 3
                validatePlayerStates(game, p, expectedSpecials, size)
        }
    }

    void testInitializeGameNoOptionalFeatures() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid20x20],
                players: [PONE, PTWO, PTHREE]
        )

        assert game.playerDetails == [:]
        int size = 20
        initializer.util = [
                getSize: {
                    GameFeature feature ->
                        assert feature == GameFeature.Grid20x20
                        return size
                }
        ] as GridSizeUtil
        initializer.initializeGame(game)
        assert game.playerDetails.size() == 3
        game.players.each {
            Player p ->
                validatePlayerStates(game, p, 0, size)
        }
    }

    protected void validatePlayerStates(TBGame game, Player p, int expectedSpecials, int size) {
        Set<ObjectId> opponentIds = game.players.collect { it.id } as Set
        opponentIds.remove(p.id)
        TBPlayerState playerState = game.playerDetails[p.id]
        assert playerState
        assert playerState.ecmsRemaining == expectedSpecials
        assert playerState.evasiveManeuversRemaining == expectedSpecials
        assert playerState.emergencyRepairsRemaining == expectedSpecials
        assert playerState.spysRemaining == expectedSpecials
        assert playerState.opponentGrids.keySet() == opponentIds
        assert playerState.opponentGrids.values().each {
            Grid it ->
                it.table.size() == size * size
                it.table.values().each {
                    GridCellState state ->
                        assert state == GridCellState.Unknown
                }
        }
        assert playerState.opponentViews.keySet() == opponentIds
        assert playerState.opponentViews.values().each {
            Grid it ->
                it.table.size() == size * size
                it.table.values().each {
                    GridCellState state ->
                        assert state == GridCellState.Unknown
                }
        }
    }

}
