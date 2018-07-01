package com.jtbdevelopment.TwistedBattleship.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.core.spring.jackson.JacksonModuleCustomization;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame;
import org.springframework.stereotype.Component;

/**
 * Date: 2/8/15
 * Time: 4:08 PM
 */
@Component
public class TBJacksonRegistration implements JacksonModuleCustomization {
    @Override
    public void customizeModule(final SimpleModule module) {
        module.addAbstractTypeMapping(GameSpecificPlayerAttributes.class, TBPlayerAttributes.class);
        module.addAbstractTypeMapping(MaskedMultiPlayerGame.class, TBMaskedGame.class);
        module.registerSubtypes(TBMaskedGame.class);
    }

}
