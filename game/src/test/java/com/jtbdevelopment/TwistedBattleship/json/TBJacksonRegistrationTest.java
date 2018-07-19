package com.jtbdevelopment.TwistedBattleship.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 4/3/15
 * Time: 7:13 PM
 */
public class TBJacksonRegistrationTest {
    @Test
    public void testCustomizeModule() {
        TBJacksonRegistration registration = new TBJacksonRegistration();
        SimpleModule module = Mockito.mock(SimpleModule.class);
        registration.customizeModule(module);
        Mockito.verify(module).addAbstractTypeMapping(GameSpecificPlayerAttributes.class, TBPlayerAttributes.class);
        Mockito.verify(module).addAbstractTypeMapping(MaskedMultiPlayerGame.class, TBMaskedGame.class);
    }

}
