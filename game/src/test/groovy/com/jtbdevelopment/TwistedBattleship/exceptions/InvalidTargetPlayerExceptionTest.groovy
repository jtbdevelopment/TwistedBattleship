package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/12/15
 * Time: 7:06 AM
 */
class InvalidTargetPlayerExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "Cannot target this player with this move." == new InvalidTargetPlayerException().message
    }
}
