package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
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
                        (PONE.id)  : new TBPlayerState(activeShipsRemaining: 2, scoreFromHits: 10),
                        (PTWO.id)  : new TBPlayerState(activeShipsRemaining: 0, scoreFromLiving: 30, scoreFromSinks: 10),
                        (PTHREE.id): new TBPlayerState(activeShipsRemaining: 1, scoreFromHits: 10, scoreFromSinks: 10),
                ]
        )
        TBMaskedGame maskedGame = masker.maskGameForPlayer(game, PONE)
        assert maskedGame
        assert maskedGame.maskedPlayersState.is(game.playerDetails[PONE.id])
        assert maskedGame.playersAlive == [(PONE.md5): true, (PTWO.md5): false, (PTHREE.md5): true]
        assert maskedGame.playersScore == [(PONE.md5): 10, (PTWO.md5): 40, (PTHREE.md5): 20]
    }
}
