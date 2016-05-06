package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/5/16
 * Time: 8:46 PM
 */
class NoCruiseMissileActionsRemainingTest extends GroovyTestCase {
    void testMessage() {
        assert "You have no more cruise missiles to fire." == new NoCruiseMissileActionsRemaining().message
    }

}
