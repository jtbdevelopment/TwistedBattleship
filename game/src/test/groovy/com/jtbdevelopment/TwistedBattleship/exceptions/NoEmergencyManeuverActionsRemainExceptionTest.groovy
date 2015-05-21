package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/20/15
 * Time: 7:16 PM
 */
class NoEmergencyManeuverActionsRemainExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "The ships cannot handler any further emergency maneuvers." == new NoEmergencyManeuverActionsRemainException().message
    }
}
