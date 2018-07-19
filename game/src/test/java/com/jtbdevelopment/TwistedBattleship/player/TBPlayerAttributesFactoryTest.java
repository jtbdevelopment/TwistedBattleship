package com.jtbdevelopment.TwistedBattleship.player;

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Date: 4/3/15
 * Time: 7:18 PM
 */
public class TBPlayerAttributesFactoryTest {
    private TBPlayerAttributesFactory factory = new TBPlayerAttributesFactory();

    @Test
    public void testNewPlayer() {
        GameSpecificPlayerAttributes attributes = factory.newPlayerAttributes();
        Assert.assertNotNull(attributes);
        assertTrue(attributes instanceof TBPlayerAttributes);
    }

    @Test
    public void testNewManualPlayer() {
        GameSpecificPlayerAttributes attributes = factory.newManualPlayerAttributes();
        Assert.assertNotNull(attributes);
        assertTrue(attributes instanceof TBPlayerAttributes);
    }

    @Test
    public void testNewSystemPlayer() {
        Assert.assertNull(factory.newSystemPlayerAttributes());
    }
}
