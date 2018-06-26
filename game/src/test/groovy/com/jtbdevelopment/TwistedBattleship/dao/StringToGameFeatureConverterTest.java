package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:51 AM
 */
public class StringToGameFeatureConverterTest {

    private StringToGameFeatureConverter converter = new StringToGameFeatureConverter();

    @Test
    public void testConverts() {
        assertEquals(GameFeature.CruiseMissileDisabled, converter.convert("CruiseMissileDisabled"));
    }

    @Test
    public void testIgnoresNull() {
        assertNull(converter.convert(null));
    }
}
