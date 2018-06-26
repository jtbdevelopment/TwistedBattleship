package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/5/16
 * Time: 8:46 PM
 */
public class NoCruiseMissileActionsRemainingTest {
    @Test
    public void testMessage() {
        assertEquals("You have no more cruise missiles to fire.", new NoCruiseMissileActionsRemaining().getMessage());
    }

}
