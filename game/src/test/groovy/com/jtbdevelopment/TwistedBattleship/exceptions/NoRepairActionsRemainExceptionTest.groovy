package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/19/15
 * Time: 6:50 AM
 */
class NoRepairActionsRemainExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "You are no out of emergency repair materials." == new NoRepairActionsRemainException().message
    }
}
