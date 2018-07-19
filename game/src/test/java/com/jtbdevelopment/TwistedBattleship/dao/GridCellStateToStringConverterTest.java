package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:53 AM
 */
public class GridCellStateToStringConverterTest {

    private GridCellStateToStringConverter converter = new GridCellStateToStringConverter();

    @Test
    public void testConverts() {
        assertEquals("KnownByHit", converter.convert(GridCellState.KnownByHit));
    }

    @Test
    public void testConvertsNull() {
        assertNull(converter.convert(null));
    }
}
