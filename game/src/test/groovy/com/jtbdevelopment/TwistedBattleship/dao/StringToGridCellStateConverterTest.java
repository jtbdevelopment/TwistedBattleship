package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:51 AM
 */
public class StringToGridCellStateConverterTest {

    private StringToGridCellStateConverter converter = new StringToGridCellStateConverter();

    @Test
    public void testConverts() {
        assertEquals(GridCellState.Unknown, converter.convert("Unknown"));
    }

    @Test
    public void testIgnoresNull() {
        assertNull(converter.convert(null));
    }
}
