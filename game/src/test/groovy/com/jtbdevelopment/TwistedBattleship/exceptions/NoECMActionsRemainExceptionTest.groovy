package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/20/15
 * Time: 6:38 PM
 */
class NoECMActionsRemainExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "You have no more ECM devices to deploy." == new NoECMActionsRemainException().message
    }
}
