package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/12/15
 * Time: 7:06 AM
 */
public class InvalidTargetPlayerExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("Cannot target this player with this move.", new InvalidTargetPlayerException().getMessage());
    }

}
