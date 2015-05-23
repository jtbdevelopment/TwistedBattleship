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

    void testAdd() {
        GridCoordinate g = gc1.add(gc5)
        assert 5 == g.row
        assert 5 == g.column
        g = gc1.add(gc4)
        assert 8 == g.row
        assert 8 == g.column
        g = gc1.add(new GridCoordinate(-1, -2))
        assert 4 == g.row
        assert 1 == g.column
    }

    void testAddDirect() {
        GridCoordinate g = gc1.add(1, 6)
        assert 6 == g.row
        assert 9 == g.column
    }

    void testToString() {
        assert "(5,3)" == gc1.toString()
        assert "(5,3)" == gc2.toString()
        assert "(3,5)" == gc3.toString()
        assert "(10,12)" == new GridCoordinate(10, 12).toString()
    }

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

    void testEqualCoordinates() {
        assert 0 == new GridCoordinate(0, 0).compareTo(new GridCoordinate(0, 0))
        assert 0 == new GridCoordinate(5, 6).compareTo(new GridCoordinate(5, 6))
    }

    void testLessThan() {
        assert 0 > new GridCoordinate(0, 0).compareTo(new GridCoordinate(2, 0))
        assert 0 > new GridCoordinate(4, 5).compareTo(new GridCoordinate(6, 3))
        assert 0 > new GridCoordinate(0, 0).compareTo(new GridCoordinate(1, 0))
    }

    void testGreaterThan() {
        assert 0 < new GridCoordinate(2, 4).compareTo(new GridCoordinate(2, 0))
        assert 0 < new GridCoordinate(9, 1).compareTo(new GridCoordinate(6, 3))
        assert 0 < new GridCoordinate(1, 1).compareTo(new GridCoordinate(1, 0))
    }
}
