package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 5/30/18 Time: 6:51 AM
 */
public class StringToActionTypeConverterTest {

    private StringToActionTypeConverter converter = new StringToActionTypeConverter();

    @Test
    public void testConverts() {
        assertEquals(TBActionLogEntry.TBActionType.CruiseMissile, converter.convert("CruiseMissile"));
    }

    @Test
    public void testIgnoresNull() {
        assertNull(converter.convert(null));
    }
}
