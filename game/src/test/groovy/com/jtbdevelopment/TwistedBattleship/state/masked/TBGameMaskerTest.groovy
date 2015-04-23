package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase

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
                                shipStates: [
                                        (Ship.Battleship): new ShipState(Ship.Battleship, null, []),
                                        (Ship.Destroyer) : new ShipState(Ship.Destroyer, null, []),
                                        (Ship.Submarine) : new ShipState(Ship.Submarine, null, []),
                                        (Ship.Carrier)   : new ShipState(Ship.Carrier, null, []),
                                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, null, [])
                                ]),
                        (PTWO.id)  : new TBPlayerState(
                                scoreFromLiving: 30, scoreFromSinks: 10,
                                shipStates: [
                                        (Ship.Battleship): new ShipState(Ship.Battleship, null, 0, [], []),
                                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, null, 0, [], [])
                                ]),
                        (PTHREE.id): new TBPlayerState(scoreFromHits: 10, scoreFromSinks: 10),
                ]
        )
        TBMaskedGame maskedGame = masker.maskGameForPlayer(game, PONE)
        assert maskedGame
        assert maskedGame.maskedPlayersState.is(game.playerDetails[PONE.id])
        assert maskedGame.playersAlive == [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false]
        assert maskedGame.playersScore == [(PONE.md5): 10, (PTWO.md5): 40, (PTHREE.md5): 20]
        assert maskedGame.playersSetup == [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): false]
    }
}
