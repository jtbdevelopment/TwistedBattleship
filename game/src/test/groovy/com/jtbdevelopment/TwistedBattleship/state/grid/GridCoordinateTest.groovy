package com.jtbdevelopment.TwistedBattleship.state.grid

/**
 * Date: 5/1/15
 * Time: 7:07 AM
 */
class GridCoordinateTest extends GroovyTestCase {
    GridCoordinate gc1 = new GridCoordinate(5, 3)
    GridCoordinate gc2 = new GridCoordinate(5, 3)
    GridCoordinate gc3 = new GridCoordinate(3, 5)
    GridCoordinate gc4 = new GridCoordinate(3, 5)
    GridCoordinate gc5 = new GridCoordinate(0, 2)

    void testEquals() {
        assert gc1 == gc2
        assert gc1 != gc3
        assert gc1 != gc4
        assert gc1 != gc5

        assert gc3 == gc4
        assert gc3 != gc2
        assert gc3 != gc5
    }

    void testHashcode() {
        assert 158 == gc1.hashCode()
        assert 98 == gc4.hashCode()
        assert gc1.hashCode() == gc2.hashCode()
        assert gc1.hashCode() != gc5.hashCode()

        assert gc3.hashCode() == gc4.hashCode()
    }
}
