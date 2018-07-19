package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Date: 5/1/15
 * Time: 7:07 AM
 */
public class GridCoordinateTest {
    private GridCoordinate gc1 = new GridCoordinate(5, 3);
    private GridCoordinate gc2 = new GridCoordinate(5, 3);
    private GridCoordinate gc3 = new GridCoordinate(3, 5);
    private GridCoordinate gc4 = new GridCoordinate(3, 5);
    private GridCoordinate gc5 = new GridCoordinate(0, 2);

    @Test
    public void testAdd() {
        GridCoordinate g = gc1.add(gc5);
        assertEquals(5, g.getRow());
        assertEquals(5, g.getColumn());
        g = gc1.add(gc4);
        assertEquals(8, g.getRow());
        assertEquals(8, g.getColumn());
        g = gc1.add(new GridCoordinate(-1, -2));
        assertEquals(4, g.getRow());
        assertEquals(1, g.getColumn());
    }

    @Test
    public void testAddDirect() {
        GridCoordinate g = gc1.add(1, 6);
        assertEquals(6, g.getRow());
        assertEquals(9, g.getColumn());
    }

    @Test
    public void testToString() {
        assertEquals("(5,3)", gc1.toString());
        assertEquals("(5,3)", gc2.toString());
        assertEquals("(3,5)", gc3.toString());
        assertEquals("(10,12)", new GridCoordinate(10, 12).toString());
    }

    @Test
    public void testEquals() {
        assertEquals(gc1, gc2);
        assertNotEquals(gc1, gc3);
        assertNotEquals(gc1, gc4);
        assertNotEquals(gc1, gc5);

        assertEquals(gc3, gc4);
        assertNotEquals(gc3, gc2);
        assertNotEquals(gc3, gc5);
    }

    @Test
    public void testHashcode() {
        assertEquals(158, gc1.hashCode());
        assertEquals(98, gc4.hashCode());
        assertEquals(gc1.hashCode(), gc2.hashCode());
        assertNotEquals(gc1.hashCode(), gc5.hashCode());

        assertEquals(gc3.hashCode(), gc4.hashCode());
    }

    @Test
    public void testEqualCoordinates() {
        assertEquals(0, new GridCoordinate(0, 0).compareTo(new GridCoordinate(0, 0)));
        assertEquals(0, new GridCoordinate(5, 6).compareTo(new GridCoordinate(5, 6)));
    }

    @Test
    public void testLessThan() {
        Assert.assertTrue(0 > new GridCoordinate(0, 0).compareTo(new GridCoordinate(2, 0)));
        Assert.assertTrue(0 > new GridCoordinate(4, 5).compareTo(new GridCoordinate(6, 3)));
        Assert.assertTrue(0 > new GridCoordinate(0, 0).compareTo(new GridCoordinate(1, 0)));
    }

    @Test
    public void testGreaterThan() {
        Assert.assertTrue(0 < new GridCoordinate(2, 4).compareTo(new GridCoordinate(2, 0)));
        Assert.assertTrue(0 < new GridCoordinate(9, 1).compareTo(new GridCoordinate(6, 3)));
        Assert.assertTrue(0 < new GridCoordinate(1, 1).compareTo(new GridCoordinate(1, 0)));
    }

    @Test
    public void testIsValidCoordinate() {
        TBGame game = new TBGame();
        game.setGridSize(7);
        Assert.assertTrue(gc1.isValidCoordinate(game));
        Assert.assertTrue(gc3.isValidCoordinate(game));
        Assert.assertTrue(gc5.isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(-1, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(5, -1).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(5, 7).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(7, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(8, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(5, 8).isValidCoordinate(game));

        game.setGridSize(8);
        Assert.assertTrue(gc1.isValidCoordinate(game));
        Assert.assertTrue(gc3.isValidCoordinate(game));
        Assert.assertTrue(gc5.isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(-1, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(5, -1).isValidCoordinate(game));
        Assert.assertTrue(new GridCoordinate(5, 7).isValidCoordinate(game));
        Assert.assertTrue(new GridCoordinate(7, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(8, 5).isValidCoordinate(game));
        Assert.assertFalse(new GridCoordinate(5, 8).isValidCoordinate(game));
    }
}
