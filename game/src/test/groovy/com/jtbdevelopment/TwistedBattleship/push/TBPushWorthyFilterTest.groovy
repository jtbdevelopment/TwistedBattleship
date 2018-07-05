package com.jtbdevelopment.TwistedBattleship.push

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import org.junit.Test

import static org.junit.Assert.assertFalse

/**
 * Date: 10/15/15
 * Time: 6:45 PM
 */
class TBPushWorthyFilterTest extends MongoGameCoreTestCase {
    TBPushWorthyFilter filter = new TBPushWorthyFilter()

//        Challenged('Challenge delivered.', 'Challenged'),  /*  Agreement from initial players  */
//    Setup('Game setup in progress.', 'Setup'), /*  Setting word phrases  */
    //   Playing('Game in play!', 'Play'),
    //   RoundOver('Round finished.', 'Played', 7),  /*  Option to continue to a new game  */

    @Test
    void testDeclined() {
        TBGame game = new TBGame(gamePhase: GamePhase.Declined)

        assertFalse filter.shouldPush(null, game)
    }

    @Test
    void testQuitAlwaysFalse() {
        TBGame game = new TBGame(gamePhase: GamePhase.Quit)

        assertFalse filter.shouldPush(null, game)
    }

    @Test
    void testNextRoundStartedAlwaysFalse() {
        TBGame game = new TBGame(gamePhase: GamePhase.NextRoundStarted)

        assertFalse filter.shouldPush(null, game)
    }

    @Test
    void testRoundOverAlwaysTrue() {
        TBGame game = new TBGame(gamePhase: GamePhase.RoundOver)

        assert filter.shouldPush(null, game)
    }

    @Test
    void testPlayingNotCurrentPlayer() {
        TBGame game = new TBGame(gamePhase: GamePhase.Playing, currentPlayer: MongoGameCoreTestCase.PONE.id)

        assertFalse filter.shouldPush(MongoGameCoreTestCase.PTHREE, game)
    }

    @Test
    void testPlayingAndIsCurrentPlayer() {
        TBGame game = new TBGame(gamePhase: GamePhase.Playing, currentPlayer: MongoGameCoreTestCase.PONE.id)

        assert filter.shouldPush(MongoGameCoreTestCase.PONE, game)
    }

    @Test
    void testChallengedAndThisPlayerPendingTrue() {
        TBGame game = new TBGame(gamePhase: GamePhase.Challenged, playerStates: [(PONE.id): PlayerState.Pending])

        assert filter.shouldPush(MongoGameCoreTestCase.PONE, game)
    }

    @Test
    void testChallengedAndThisPlayerNotPendingIsFalse() {
        TBGame game = new TBGame(gamePhase: GamePhase.Challenged, playerStates: [(PONE.id): PlayerState.Accepted])
        assertFalse filter.shouldPush(MongoGameCoreTestCase.PONE, game)
        game.playerStates[PONE.id] = PlayerState.Rejected
        assertFalse filter.shouldPush(MongoGameCoreTestCase.PONE, game)
    }

    @Test
    void testSetupAndCurrentPlayerNotSetupIsTrue() {
        TBGame game = new TBGame(gamePhase: GamePhase.Setup, playerDetails: [(PONE.id): [isSetup: {
            return false
        }] as TBPlayerState])
        assert filter.shouldPush(MongoGameCoreTestCase.PONE, game)
    }

    @Test
    void testSetupAndCurrentPlayerIsSetupIsFalse() {
        TBGame game = new TBGame(gamePhase: GamePhase.Setup, playerDetails: [(PONE.id): [isSetup: {
            return true
        }] as TBPlayerState])
        assertFalse filter.shouldPush(MongoGameCoreTestCase.PONE, game)
    }
}
