package com.jtbdevelopment.TwistedBattleship.state.grid

/**
 * Date: 4/6/15
 * Time: 7:13 PM
 */
class GridTest extends GroovyTestCase {
    void testInitialization() {
        Grid grid = new Grid(5)
        assert grid.size == 5
        (0..4).each {
            int r ->
                (0..4).each {
                    int c ->
                        assert grid.get(r, c) == GridCellState.Unknown
                }
        }
    }

    void testSetGetInBounds() {
        Grid grid = new Grid(5)

        def row = 3
        def column = 4
        assert GridCellState.Unknown == grid.get(row, column)

        def state = GridCellState.KnownEmpty
        assert grid.is(grid.set(row, column, state))
        assert state == grid.get(row, column)
        (0..4).each {
            int r ->
                (0..4).each {
                    int c ->
                        if (r == row && c == column) {
                            assert grid.get(r, c) == state
                        } else {
                            assert grid.get(r, c) == GridCellState.Unknown
                        }
                }
        }
    }

    void testGetOutsideBounds() {
        Grid grid = new Grid(5)
        shouldFail(IndexOutOfBoundsException.class, {
            grid.get(3, 10)
        })
    }

    void testSetOutsideBounds() {
        Grid grid = new Grid(5)
        shouldFail(IndexOutOfBoundsException.class, {
            grid.set(10, 3, GridCellState.KnownByHit)
        })
    }

    void testCoordinateVsActual() {
        Grid grid = new Grid(5)
        grid.set(1, 1, GridCellState.KnownByHit)
        assert GridCellState.KnownByHit == grid.get(new GridCoordinate(1, 1))

        grid.set(new GridCoordinate(4, 4), GridCellState.KnownShip)
        assert GridCellState.KnownShip == grid.get(4, 4)
    }

    void testHashCode() {
        Grid g1 = new Grid(5)
        Grid g2 = new Grid(10)
        Grid g3 = new Grid(5)
        assert g1.hashCode() == g3.hashCode()
        assert g1.hashCode() != g2.hashCode()
    }

    void testEquals() {
        Grid g1 = new Grid(5)
        Grid g2 = new Grid(10)
        Grid g3 = new Grid(5)
        assert g1 == g3
        assert g1 != g2

        g1.set(1, 4, GridCellState.KnownByHit)
        assert g1 != g3
        g3.set(1, 4, GridCellState.KnownByHit)
        assert g1 == g3

        g3.set(3, 0, GridCellState.KnownShip)
        assert g1 != g3
    }
}
