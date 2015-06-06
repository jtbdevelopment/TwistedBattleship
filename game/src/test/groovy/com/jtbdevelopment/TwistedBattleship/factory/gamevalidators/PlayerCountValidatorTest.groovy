package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 6/5/15
 * Time: 10:11 PM
 */
class PlayerCountValidatorTest extends MongoGameCoreTestCase {
    PlayerCountValidator validator = new PlayerCountValidator()

    void testTooFewPlayers() {
        assertFalse validator.validateGame(new TBGame())
        assertFalse validator.validateGame(new TBGame(players: [PONE]))
    }

    void testTooManyPlayers() {
        assertFalse validator.validateGame(new TBGame(players: [PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1, PINACTIVE2]))
    }

    void testAtLeastTwoPlayersOK() {
        assert validator.validateGame(new TBGame(players: [PONE, PTWO]))
    }

    void testAtMostSixPlayersOK() {
        assert validator.validateGame(new TBGame(players: [PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1]))
    }

    void testMessage() {
        assert "A game consists of at least two players and at most six players." == validator.errorMessage()
    }
}
