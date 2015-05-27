package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 5/26/15
 * Time: 6:40 PM
 */
class ShipRotaterTest extends GroovyTestCase {
    ShipRotater rotater = new ShipRotater()

    Map<List<GridCoordinate>, List<GridCoordinate>> EXPECTED = [
            //  Vertical to Horizontal
            [new GridCoordinate(0, 1), new GridCoordinate(0, 2)]                                                                                        :
                    [new GridCoordinate(-1, 2), new GridCoordinate(0, 2)],
            [new GridCoordinate(7, 7), new GridCoordinate(7, 8), new GridCoordinate(7, 9)]                                                              :
                    [new GridCoordinate(6, 8), new GridCoordinate(7, 8), new GridCoordinate(8, 8)],
            [new GridCoordinate(4, 3), new GridCoordinate(4, 4), new GridCoordinate(4, 5), new GridCoordinate(4, 6)]                                    :
                    [new GridCoordinate(2, 5), new GridCoordinate(3, 5), new GridCoordinate(4, 5), new GridCoordinate(5, 5)],
            [new GridCoordinate(4, 3), new GridCoordinate(4, 4), new GridCoordinate(4, 5), new GridCoordinate(4, 6), new GridCoordinate(4, 7)]          :
                    [new GridCoordinate(2, 5), new GridCoordinate(3, 5), new GridCoordinate(4, 5), new GridCoordinate(5, 5), new GridCoordinate(6, 5)],

            //  Horizontal to Vertical
            [new GridCoordinate(10, 10), new GridCoordinate(11, 10)]                                                                                    :
                    [new GridCoordinate(11, 9), new GridCoordinate(11, 10)],
            [new GridCoordinate(9, 10), new GridCoordinate(10, 10), new GridCoordinate(11, 10)]                                                         :
                    [new GridCoordinate(10, 9), new GridCoordinate(10, 10), new GridCoordinate(10, 11)],
            [new GridCoordinate(5, 6), new GridCoordinate(6, 6), new GridCoordinate(7, 6), new GridCoordinate(8, 6)]                                    :
                    [new GridCoordinate(7, 4), new GridCoordinate(7, 5), new GridCoordinate(7, 6), new GridCoordinate(7, 7)],
            [new GridCoordinate(11, 15), new GridCoordinate(12, 15), new GridCoordinate(13, 15), new GridCoordinate(14, 15), new GridCoordinate(15, 15)]:
                    [new GridCoordinate(13, 13), new GridCoordinate(13, 14), new GridCoordinate(13, 15), new GridCoordinate(13, 16), new GridCoordinate(13, 17)],
    ]

    void testRotateShip() {
        EXPECTED.each {
            assert rotater.rotateShip(new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>(it.key))) == it.value
        }
    }
}
