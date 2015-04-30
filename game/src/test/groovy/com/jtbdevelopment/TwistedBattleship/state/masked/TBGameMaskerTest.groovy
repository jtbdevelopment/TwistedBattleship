package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
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
                players: [PONE, PTWO, PTHREE],
                initiatingPlayer: PTHREE.id,
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(
                                scoreFromHits: 10,
                                scoreFromLiving: 20,
                                scoreFromSinks: 40,
                                spysRemaining: 1,
                                ecmsRemaining: 2,
                                evasiveManeuversRemaining: 3,
                                emergencyRepairsRemaining: 4,
                                opponentViews: [
                                        (PTWO.id)  : new Grid(10),
                                        (PTHREE.id): new Grid(10)
                                ],
                                opponentGrids: [
                                        (PTWO.id)  : new Grid(10),
                                        (PTHREE.id): new Grid(10)
                                ],
                                shipStates: [
                                        (Ship.Battleship): new ShipState(Ship.Battleship, []),
                                        (Ship.Destroyer) : new ShipState(Ship.Destroyer, []),
                                        (Ship.Submarine) : new ShipState(Ship.Submarine, []),
                                        (Ship.Carrier)   : new ShipState(Ship.Carrier, []),
                                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, [])
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
        game.playerDetails[PONE.id].opponentGrids[PTWO.id].set(3, 5, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentGrids[PTHREE.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(0, 2, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(1, 4, GridCellState.KnownByMiss)
        TBMaskedGame maskedGame = masker.maskGameForPlayer(game, PONE)
        assert maskedGame
        assert maskedGame.playersAlive == [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false]
        assert maskedGame.playersScore == [(PONE.md5): 70, (PTWO.md5): 40, (PTHREE.md5): 20]
        assert maskedGame.playersSetup == [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false]

        TBPlayerState playerState = game.playerDetails[PONE.id]
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
        playerState.opponentGrids.each {
            ObjectId id, Grid grid ->
                Player p = game.players.find { it.id == id }
                assert grid == maskedGame.maskedPlayersState.opponentGrids[p.md5]
        }
        playerState.opponentViews.each {
            ObjectId id, Grid grid ->
                Player p = game.players.find { it.id == id }
                assert grid == maskedGame.maskedPlayersState.opponentViews[p.md5]
        }
    }
}
