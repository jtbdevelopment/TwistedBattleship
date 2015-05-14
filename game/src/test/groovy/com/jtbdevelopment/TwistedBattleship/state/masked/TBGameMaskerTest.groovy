package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
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
        masker.gridSizeUtil = new GridSizeUtil()
        TBGame game = new TBGame(
                rematchTimestamp: ZonedDateTime.now(),
                gamePhase: GamePhase.Playing,
                features: [GameFeature.Grid10x10],
                players: [PONE, PTWO, PTHREE],
                initiatingPlayer: PTHREE.id,
                generalMessage: "All hands on deck!",
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(
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
                                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                                        (Ship.Destroyer) : new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>()),
                                        (Ship.Submarine) : new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                                        (Ship.Carrier)   : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>())
                                ]),
                        (PTWO.id)  : new TBPlayerState(
                                scoreFromLiving: 30, scoreFromSinks: 10,
                                shipStates: [
                                        (Ship.Battleship): new ShipState(Ship.Battleship, 0, [], []),
                                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, 0, [], [])
                                ]),
                        (PTHREE.id): new TBPlayerState(scoreFromHits: 10, scoreFromSinks: 10),
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
        assert "All hands on deck!" == maskedGame.generalMessage
        assert [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false] == maskedGame.playersAlive
        assert [(PONE.md5): 70, (PTWO.md5): 40, (PTHREE.md5): 20] == maskedGame.playersScore
        assert [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false] == maskedGame.playersSetup

        TBPlayerState playerState = game.playerDetails[PONE.id]
        assert maskedGame.maskedPlayersState.lastActionMessage == playerState.lastActionMessage
        assert maskedGame.maskedPlayersState.shipStates == playerState.shipStates
        assert maskedGame.maskedPlayersState.alive == playerState.alive
        assert maskedGame.maskedPlayersState.setup == playerState.setup
        assert maskedGame.maskedPlayersState.totalScore == playerState.totalScore
        assert maskedGame.maskedPlayersState.activeShipsRemaining == playerState.activeShipsRemaining
        assert maskedGame.maskedPlayersState.spysRemaining == playerState.spysRemaining
        assert maskedGame.maskedPlayersState.evasiveManeuversRemaining == playerState.evasiveManeuversRemaining
        assert maskedGame.maskedPlayersState.ecmsRemaining == playerState.ecmsRemaining
        assert maskedGame.maskedPlayersState.emergencyRepairsRemaining == playerState.emergencyRepairsRemaining
        assert maskedGame.maskedPlayersState.shipStates == playerState.shipStates
        assert maskedGame.maskedPlayersState.opponentViews.size() == 2
        assert maskedGame.maskedPlayersState.opponentGrids.size() == 2
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
    }
}
