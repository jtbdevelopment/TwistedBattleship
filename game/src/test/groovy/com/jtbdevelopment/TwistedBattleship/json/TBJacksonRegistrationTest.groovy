package com.jtbdevelopment.TwistedBattleship.json

import com.fasterxml.jackson.databind.module.SimpleModule
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame

/**
 * Date: 4/3/15
 * Time: 7:13 PM
 */
class TBJacksonRegistrationTest extends GroovyTestCase {
    void testCustomizeModule() {
        TBJacksonRegistration registration = new TBJacksonRegistration()
        boolean registeredGameAttributes = false
        boolean registeredMaskedGame = false
        def module = [
                addAbstractTypeMapping: {
                    Class iface, Class impl ->
                        if (GameSpecificPlayerAttributes.class.is(iface)) {
                            assert TBPlayerAttributes.class.is(impl)
                            registeredGameAttributes = true
                            return null
                        }
                        if (MaskedMultiPlayerGame.class.is(iface)) {
                            assert TBMaskedGame.class.is(impl)
                            registeredMaskedGame = true
                            return null
                        }
                        fail('unexpected attributes')
                }
        ] as SimpleModule
        registration.customizeModule(module)
        assert registeredGameAttributes
        assert registeredMaskedGame
    }
}
