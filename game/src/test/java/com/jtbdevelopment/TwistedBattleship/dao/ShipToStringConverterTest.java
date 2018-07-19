package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:53 AM
 */
public class ShipToStringConverterTest {

    private ShipToStringConverter converter = new ShipToStringConverter();

    @Test
    public void testConverts() {
        assertEquals("Cruiser", converter.convert(Ship.Cruiser));
    }

    @Test
    public void testConvertsNull() {
        assertNull(converter.convert(null));
    }
}
