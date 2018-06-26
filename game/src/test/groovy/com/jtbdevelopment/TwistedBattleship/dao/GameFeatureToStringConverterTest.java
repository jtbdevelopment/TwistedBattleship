package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:53 AM
 */
public class GameFeatureToStringConverterTest {

    private GameFeatureToStringConverter converter = new GameFeatureToStringConverter();

    @Test
    public void testConverts() {
        assertEquals("FogOfWar", converter.convert(GameFeature.FogOfWar));
    }

    @Test
    public void testConvertsNull() {
        assertNull(converter.convert(null));
    }
}
