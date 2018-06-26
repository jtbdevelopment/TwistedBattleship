package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 5/20/15
 * Time: 6:38 PM
 */
public class NoECMActionsRemainExceptionTest {
    @Test
    public void testMessage() {
        Assert.assertEquals("You have no more ECM devices to deploy.", new NoECMActionsRemainException().getMessage());
    }

}
