package com.jtbdevelopment.TwistedBattleship.state;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 4/2/15
 * Time: 6:42 PM
 */
public class TBGameTest {
    private TBGame game = new TBGame();

    @Test
    public void testInitialize() {
        assertEquals(new LinkedHashMap(), game.getPlayerDetails());
        assertEquals(new ArrayList(), game.getStartingShips());
        assertNull(game.getCurrentPlayer());
        assertEquals(0, game.getGridSize());
        assertNull(game.getRematchTimestamp());
        assertNull(game.getPreviousId());
    }
}
