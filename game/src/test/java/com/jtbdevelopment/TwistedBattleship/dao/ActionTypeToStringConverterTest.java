package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:53 AM
 */
public class ActionTypeToStringConverterTest {

    private ActionTypeToStringConverter converter = new ActionTypeToStringConverter();

    @Test
    public void testConverts() {
        assertEquals("Victory", converter.convert(TBActionLogEntry.TBActionType.Victory));
    }

    @Test
    public void testConvertsNull() {
        assertNull(converter.convert(null));
    }
}
