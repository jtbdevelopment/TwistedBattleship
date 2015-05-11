package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/8/15
 * Time: 7:07 AM
 */
class NotEnoughActionsForSpecialExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "Not enough actions remain to use a special." == new NotEnoughActionsForSpecialException().message
    }
}
