package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:51 AM
 */
public class StringToShipConverterTest {

    private StringToShipConverter converter = new StringToShipConverter();

    @Test
    public void testConverts() {
        assertEquals(Ship.Submarine, converter.convert("Submarine"));
    }

    @Test
    public void testIgnoresNull() {
        assertNull(converter.convert(null));
    }
}
