package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import org.bson.types.ObjectId

import java.time.ZonedDateTime

/**
 * Date: 4/2/15
 * Time: 6:48 PM
 */
class TBGameMaskerTest extends MongoGameCoreTestCase {
    TBGameMasker masker = new TBGameMasker()

    void testMaskingGame() {
        TBGame game = new TBGame(
                rematchTimestamp: ZonedDateTime.now(),
                gamePhase: GamePhase.Playing,
                features: [GameFeature.Grid10x10],
                gridSize: 10,
                startingShips: Ship.values().toList(),
                players: [PONE, PTWO, PTHREE],
                currentPlayer: PTHREE.id,
                movesForSpecials: -2,
                remainingMoves: 4,
                initiatingPlayer: PTHREE.id,
                generalMessage: "All hands on deck!",
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(
                                startingShips: Ship.values().toList(),
                                scoreFromHits: 10,
                                scoreFromLiving: 20,
                                scoreFromSinks: 40,
                                spysRemaining: 1,
                                ecmsRemaining: 2,
                                evasiveManeuversRemaining: 3,
                                emergencyRepairsRemaining: 4,
                                lastActionMessage: "P1 MESSAGE",
                                opponentViews: [
                                        (PTWO.id)  : new Grid(10),
                                        (PTHREE.id): new Grid(10)
                                ],
                                opponentGrids: [
                                        (PTWO.id)  : new Grid(10),
                                        (PTHREE.id): new Grid(10)
                                ],
                                shipStates: [
                                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>()),
                                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>())
                                ]),
                        (PTWO.id)  : new TBPlayerState(
                                startingShips: Ship.values().toList(),
                                scoreFromLiving: 30, scoreFromSinks: 10,
                                shipStates: [
                                        new ShipState(Ship.Battleship, 0, [], []),
                                        new ShipState(Ship.Cruiser, 0, [], [])
                                ]),
                        (PTHREE.id): new TBPlayerState(scoreFromHits: 10, scoreFromSinks: 10, startingShips: Ship.values().toList()),
                ]
        )
        game.playerDetails[PONE.id].opponentGrids[PTWO.id].set(4, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentGrids[PTHREE.id].set(4, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 1, GridCellState.KnownByMiss)

        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 2, GridCellState.Unknown)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 2, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 3, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 3, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 4, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 4, GridCellState.KnownByRehit)

        TBMaskedGame maskedGame = masker.maskGameForPlayer(game, PONE)
        assert maskedGame
        assert game.gridSize == maskedGame.gridSize
        assert game.movesForSpecials == maskedGame.movesForSpecials
        assert game.startingShips == maskedGame.startingShips
        assert "All hands on deck!" == maskedGame.generalMessage
        assert [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false] == maskedGame.playersAlive
        assert [(PONE.md5): 70, (PTWO.md5): 40, (PTHREE.md5): 20] == maskedGame.playersScore
        assert [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false] == maskedGame.playersSetup

        TBPlayerState playerState = game.playerDetails[PONE.id]
        assert playerState.lastActionMessage == maskedGame.maskedPlayersState.lastActionMessage
        assert playerState.shipStates == maskedGame.maskedPlayersState.shipStates
        assert playerState.startingShips == maskedGame.maskedPlayersState.startingShips
        assert playerState.alive == maskedGame.maskedPlayersState.alive
        assert playerState.setup == maskedGame.maskedPlayersState.setup
        assert playerState.totalScore == maskedGame.maskedPlayersState.totalScore
        assert playerState.activeShipsRemaining == maskedGame.maskedPlayersState.activeShipsRemaining
        assert playerState.spysRemaining == maskedGame.maskedPlayersState.spysRemaining
        assert playerState.evasiveManeuversRemaining == maskedGame.maskedPlayersState.evasiveManeuversRemaining
        assert playerState.ecmsRemaining == maskedGame.maskedPlayersState.ecmsRemaining
        assert playerState.emergencyRepairsRemaining == maskedGame.maskedPlayersState.emergencyRepairsRemaining
        assert playerState.shipStates == maskedGame.maskedPlayersState.shipStates
        assert 2 == maskedGame.maskedPlayersState.opponentViews.size()
        assert 2 == maskedGame.maskedPlayersState.opponentGrids.size()
        assert 4 == maskedGame.remainingMoves
        assert PTHREE.md5 == maskedGame.currentPlayer
        playerState.opponentGrids.each {
            ObjectId id, Grid grid ->
                assertNotNull grid
                Player p = game.players.find { it.id == id }
                assert grid == maskedGame.maskedPlayersState.opponentGrids[p.md5]
        }
        playerState.opponentViews.each {
            ObjectId id, Grid grid ->
                assertNotNull grid
                Player p = game.players.find { it.id == id }
                assert grid == maskedGame.maskedPlayersState.opponentViews[p.md5]
        }
        assertNotNull maskedGame.maskedPlayersState.consolidatedOpponentView
        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {
                def state = maskedGame.maskedPlayersState.consolidatedOpponentView.get(row, col)
                if (row != 5 || col > 4) {
                    assert state == GridCellState.Unknown
                } else {
                    switch (col) {
                        case 0:
                            assert GridCellState.KnownByHit == state
                            break
                        case 1:
                            assert GridCellState.KnownByMiss == state;
                            break
                        case 2:
                            assert GridCellState.KnownShip == state;
                            break
                        case 3:
                            assert GridCellState.KnownByHit == state;
                            break
                        case 4:
                            assert GridCellState.KnownByHit == state
                            break
                    }
                }
            }
        }
        assert PONE.md5 == maskedGame.winningPlayer
    }
}
