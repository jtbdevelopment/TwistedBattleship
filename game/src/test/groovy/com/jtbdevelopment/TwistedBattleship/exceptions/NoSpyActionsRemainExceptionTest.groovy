package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/15/15
 * Time: 7:04 AM
 */
class NoSpyActionsRemainExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "You are out of spy drones." == new NoSpyActionsRemainException().message
    }
}
