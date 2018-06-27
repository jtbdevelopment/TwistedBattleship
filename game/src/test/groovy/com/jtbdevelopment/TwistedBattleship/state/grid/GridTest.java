package com.jtbdevelopment.TwistedBattleship.state.grid;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Date: 4/6/15
 * Time: 7:13 PM
 */
public class GridTest {
    @Test
    public void testInitialization() {
        final Grid grid = new Grid(5);
        assert grid.getSize() == 5;
        for (int row = 0; row < 5; ++row) {
            for (int col = 0; col < 5; ++col) {
                assertEquals(GridCellState.Unknown, grid.get(row, col));

            }
        }
    }

    @Test
    public void testSetGetInBounds() {
        Grid grid = new Grid(5);

        int row = 3;
        int column = 4;
        assertEquals(GridCellState.Unknown, grid.get(row, column));

        GridCellState state = GridCellState.KnownEmpty;
        assertSame(grid, grid.set(row, column, state));
        assertEquals(state, grid.get(row, column));
        for (int r = 0; r < 5; ++r) {
            for (int c = 0; c < 5; ++c) {
                if (r == row && c == column) {
                    assertEquals(state, grid.get(r, c));
                } else {
                    assertEquals(GridCellState.Unknown, grid.get(r, c));
                }
            }
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutsideBounds() {
        Grid grid = new Grid(5);
        grid.get(3, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetOutsideBounds() {
        Grid grid = new Grid(5);
        grid.set(10, 3, GridCellState.KnownByHit);
    }

    @Test
    public void testCoordinateVsActual() {
        Grid grid = new Grid(5);
        grid.set(1, 1, GridCellState.KnownByHit);
        assertEquals(GridCellState.KnownByHit, grid.get(new GridCoordinate(1, 1)));

        grid.set(new GridCoordinate(4, 4), GridCellState.KnownShip);
        assertEquals(GridCellState.KnownShip, grid.get(4, 4));
    }

    @Test
    public void testHashCode() {
        Grid g1 = new Grid(5);
        Grid g2 = new Grid(10);
        Grid g3 = new Grid(5);
        assertEquals(g1.hashCode(), g3.hashCode());
        assertNotEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    public void testEquals() {
        Grid g1 = new Grid(5);
        Grid g2 = new Grid(10);
        Grid g3 = new Grid(5);
        assertEquals(g1, g3);
        assertNotEquals(g1, g2);

        g1.set(1, 4, GridCellState.KnownByHit);
        assertNotEquals(g1, g3);
        g3.set(1, 4, GridCellState.KnownByHit);
        assertEquals(g1, g3);

        g3.set(3, 0, GridCellState.KnownShip);
        assertNotEquals(g1, g3);
    }

}
