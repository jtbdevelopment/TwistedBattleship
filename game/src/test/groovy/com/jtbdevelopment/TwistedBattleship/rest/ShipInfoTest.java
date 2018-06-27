package com.jtbdevelopment.TwistedBattleship.rest;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Date: 7/1/15
 * Time: 6:51 PM
 */
public class ShipInfoTest {
    @Test
    public void testEquals() {
        assertEquals(new ShipInfo(Ship.Battleship), new ShipInfo(Ship.Battleship));
        assertNotEquals(new ShipInfo(Ship.Battleship), new ShipInfo(Ship.Carrier));
        assertNotEquals(new ShipInfo(Ship.Destroyer), new ShipInfo(Ship.Carrier));
        assertEquals(new ShipInfo(Ship.Destroyer), new ShipInfo(Ship.Destroyer));
    }

    @Test
    public void testHashCode() {
        assertEquals(new ShipInfo(Ship.Battleship).hashCode(), new ShipInfo(Ship.Battleship).hashCode());
        assertNotEquals(new ShipInfo(Ship.Battleship).hashCode(), new ShipInfo(Ship.Carrier).hashCode());
        assertNotEquals(new ShipInfo(Ship.Destroyer).hashCode(), new ShipInfo(Ship.Carrier).hashCode());
        assertEquals(new ShipInfo(Ship.Destroyer).hashCode(), new ShipInfo(Ship.Destroyer).hashCode());
    }

}
