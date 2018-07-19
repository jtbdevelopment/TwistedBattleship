package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 8/20/15
 * Time: 7:01 AM
 */
public class NotAValidThemeExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("This is not a valid theme choice.", new NotAValidThemeException().getMessage());
    }

}
