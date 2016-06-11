package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import org.bson.types.ObjectId

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class PlayerGameStateInitializerTest extends MongoGameCoreTestCase {
    PlayerGameStateInitializer initializer = new PlayerGameStateInitializer()

    void testGetOrder() {
        assert GameInitializer.LATE_ORDER == initializer.order
    }

    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE],
                gridSize: 12,
                startingShips: Ship.values().toList()
        )

        assert [:] == game.playerDetails
        initializer.initializeGame(game)
        assert 3 == game.playerDetails.size()
        game.startingShips.eachWithIndex { Ship ship, int index ->
            def expectedCoordinates = (1..ship.gridSize).collect {
                int gridIndex ->
                    new GridCoordinate(index, gridIndex)
            }
            game.playerDetails.forEach {
                key, value ->
                    assert ship == value.shipStates[index].ship
                    assert expectedCoordinates == value.shipStates[index].shipGridCells
            }
        }

        game.players.each {
            Player p ->
                def expectedSpecials = 2
                validatePlayerStates(game, p, expectedSpecials)
        }
    }

    void testInitializeGameNoOptionalFeatures() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid20x20],
                players: [PTWO, PTHREE, PONE],
                gridSize: 20,
                startingShips: Ship.values().toList()
        )

        assert game.playerDetails == [:]
        initializer.initializeGame(game)
        assert 3 == game.playerDetails.size()
        game.players.each {
            Player p ->
                validatePlayerStates(game, p, 0)
        }
    }

    protected void validatePlayerStates(final TBGame game, final Player p, final int expectedSpecials) {
        Set<ObjectId> opponentIds = game.players.collect { it.id } as Set
        opponentIds.remove(p.id)
        TBPlayerState playerState = game.playerDetails[p.id]
        assert playerState
        assert expectedSpecials == playerState.ecmsRemaining
        assert expectedSpecials == playerState.evasiveManeuversRemaining
        assert expectedSpecials == playerState.emergencyRepairsRemaining
        assert expectedSpecials == playerState.spysRemaining
        assert ((expectedSpecials > 0) ? 1 : 0) == playerState.cruiseMissilesRemaining
        assert opponentIds == playerState.opponentGrids.keySet()
        assert game.startingShips == playerState.startingShips
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
