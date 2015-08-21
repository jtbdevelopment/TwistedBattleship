package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import org.bson.types.ObjectId

/**
 * Date: 8/20/15
 * Time: 6:43 AM
 */
class TBPlayerAttributesTest extends MongoGameCoreTestCase {
    void testSetPlayer() {
        Player p = makeSimplePlayer(new ObjectId().toString())
        p.payLevel = PlayerPayLevel.FreeToPlay
        assert new TBPlayerAttributes(player: p).maxDailyFreeGames == 10
        p.payLevel = PlayerPayLevel.PremiumPlayer
        assert new TBPlayerAttributes(player: p).maxDailyFreeGames == 25
    }

    void testDefaultThemes() {
        TBPlayerAttributes attributes = new TBPlayerAttributes()
        assert 'default' == attributes.theme
        assert ['default'] == attributes.availableThemes
    }
}
