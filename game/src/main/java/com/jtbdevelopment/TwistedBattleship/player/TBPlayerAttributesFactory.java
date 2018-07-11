package com.jtbdevelopment.TwistedBattleship.player;

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributesFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 2/2/15
 * Time: 5:37 PM
 */
@Component
public class TBPlayerAttributesFactory implements GameSpecificPlayerAttributesFactory {
    @Override
    public GameSpecificPlayerAttributes newPlayerAttributes() {
        return new TBPlayerAttributes();
    }

    @Override
    public GameSpecificPlayerAttributes newManualPlayerAttributes() {
        return new TBPlayerAttributes();
    }

    @Override
    public GameSpecificPlayerAttributes newSystemPlayerAttributes() {
        return null;
    }

}
