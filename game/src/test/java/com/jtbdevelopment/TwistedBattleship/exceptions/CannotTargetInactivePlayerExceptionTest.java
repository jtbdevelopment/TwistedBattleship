package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 5/15/15
 * Time: 3:11 PM
 */
public class CannotTargetInactivePlayerExceptionTest {
    @Test
    public void testMessage() {
        Assert.assertEquals("Can only target active players.", new CannotTargetInactivePlayerException().getMessage());
    }

}
