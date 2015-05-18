package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/15/15
 * Time: 3:11 PM
 */
class CannotTargetInactivePlayerExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "Can only target active players." == new CannotTargetInactivePlayerException().message
    }
}
