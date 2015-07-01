package com.jtbdevelopment.TwistedBattleship.rest

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

/**
 * Date: 7/1/15
 * Time: 6:51 PM
 */
class ShipInfoTest extends GroovyTestCase {
    void testEquals() {
        assert new ShipInfo(Ship.Battleship) == new ShipInfo(Ship.Battleship)
        assert new ShipInfo(Ship.Battleship) != new ShipInfo(Ship.Carrier)
        assert new ShipInfo(Ship.Destroyer) != new ShipInfo(Ship.Carrier)
        assert new ShipInfo(Ship.Destroyer) == new ShipInfo(Ship.Destroyer)
    }

    void testHashCode() {
        assert new ShipInfo(Ship.Battleship).hashCode() == new ShipInfo(Ship.Battleship).hashCode()
        assert new ShipInfo(Ship.Battleship).hashCode() != new ShipInfo(Ship.Carrier).hashCode()
        assert new ShipInfo(Ship.Destroyer).hashCode() != new ShipInfo(Ship.Carrier).hashCode()
        assert new ShipInfo(Ship.Destroyer).hashCode() == new ShipInfo(Ship.Destroyer).hashCode()
    }
}
