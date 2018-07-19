package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Date: 6/5/15
 * Time: 10:11 PM
 */
public class PlayerCountValidatorTest extends MongoGameCoreTestCase {
    private PlayerCountValidator validator = new PlayerCountValidator();

    @Test
    public void testTooFewPlayers() {
        assertFalse(validator.validateGame(new TBGame()));
        TBGame game = new TBGame();
        game.setPlayers(Collections.singletonList(PONE));
        assertFalse(validator.validateGame(game));
    }

    @Test
    public void testTooManyPlayers() {
        TBGame game = new TBGame();
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1, PINACTIVE2));
        assertFalse(validator.validateGame(game));
    }

    @Test
    public void testAtLeastTwoPlayersOK() {
        TBGame game = new TBGame();
        game.setPlayers(Arrays.asList(PONE, PTWO));
        assertTrue(validator.validateGame(game));
    }

    @Test
    public void testAtMostSixPlayersOK() {
        TBGame game = new TBGame();
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE, PFOUR, PFIVE, PINACTIVE1));
        assertTrue(validator.validateGame(game));

    }

    @Test
    public void testMessage() {
        assertEquals("A game consists of at least two players and at most six players.", validator.errorMessage());
    }
}
