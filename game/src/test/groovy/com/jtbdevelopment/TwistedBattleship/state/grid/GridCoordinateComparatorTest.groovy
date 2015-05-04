package com.jtbdevelopment.TwistedBattleship.state.grid

/**
 * Date: 5/1/15
 * Time: 7:54 PM
 */
class GridCoordinateComparatorTest extends GroovyTestCase {
    GridCoordinateComparator comparator = new GridCoordinateComparator()

    void testEqualCoordinates() {
        assert 0 == comparator.compare(new GridCoordinate(0, 0), new GridCoordinate(0, 0))
        assert 0 == comparator.compare(new GridCoordinate(5, 6), new GridCoordinate(5, 6))
    }

    void testLessThan() {
        assert 0 > comparator.compare(new GridCoordinate(0, 0), new GridCoordinate(2, 0))
        assert 0 > comparator.compare(new GridCoordinate(4, 5), new GridCoordinate(6, 3))
        assert 0 > comparator.compare(new GridCoordinate(0, 0), new GridCoordinate(1, 0))
    }

    void testGreaterThan() {
        assert 0 < comparator.compare(new GridCoordinate(2, 4), new GridCoordinate(2, 0))
        assert 0 < comparator.compare(new GridCoordinate(9, 1), new GridCoordinate(6, 3))
        assert 0 < comparator.compare(new GridCoordinate(1, 1), new GridCoordinate(1, 0))
    }
}
