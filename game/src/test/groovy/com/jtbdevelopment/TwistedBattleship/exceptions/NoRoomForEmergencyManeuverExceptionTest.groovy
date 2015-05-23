package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/22/15
 * Time: 7:02 PM
 */
class NoRoomForEmergencyManeuverExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "There is no room for emergency maneuvers for this ship." == new NoRoomForEmergencyManeuverException().message
    }
}
