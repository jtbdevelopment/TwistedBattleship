package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.junit.Test

import static org.junit.Assert.assertFalse

/**
 * Date: 6/5/15
 * Time: 10:11 PM
 */
class PlayerCountValidatorTest extends MongoGameCoreTestCase {
    PlayerCountValidator validator = new PlayerCountValidator()

    @Test
    void testTooFewPlayers() {
        assertFalse validator.validateGame(new TBGame())
        assertFalse validator.validateGame(new TBGame(players: [PONE]))
    }

    @Test
    void testTooManyPlayers() {
        assertFalse validator.validateGame(new TBGame(players: [PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1, PINACTIVE2]))
    }

    @Test
    void testAtLeastTwoPlayersOK() {
        assert validator.validateGame(new TBGame(players: [PONE, PTWO]))
    }

    @Test
    void testAtMostSixPlayersOK() {
        assert validator.validateGame(new TBGame(players: [PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1]))
    }

    @Test
    void testMessage() {
        assert "A game consists of at least two players and at most six players." == validator.errorMessage()
    }
}
