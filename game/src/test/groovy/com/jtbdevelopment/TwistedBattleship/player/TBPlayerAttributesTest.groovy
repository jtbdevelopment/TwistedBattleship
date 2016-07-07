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

    void testSetTypePlayer() {
        Player p = makeSimplePlayer(new ObjectId().toString())
        p.payLevel = PlayerPayLevel.FreeToPlay
        assert new TBPlayerAttributes(player: p).maxDailyFreeGames == 50
        p.payLevel = PlayerPayLevel.PremiumPlayer
        assert new TBPlayerAttributes(player: p).maxDailyFreeGames == 100
    }

    void testDefaults() {
        TBPlayerAttributes attributes = new TBPlayerAttributes()
        assert 'default-theme' == attributes.theme
        assert ['default-theme', 'pirate-theme'] as Set == attributes.availableThemes

        assert 0 == attributes.wins
        assert 0 == attributes.losses
        assert 0 == attributes.currentWinStreak
        assert 0 == attributes.highestScore
    }

    void testAddsFreeThemesIfNotInSetIfSettingThemes() {
        TBPlayerAttributes attributes = new TBPlayerAttributes()
        attributes.availableThemes = ['1', '2'] as Set
        assert ['default-theme', 'pirate-theme', '1', '2'] as Set == attributes.availableThemes
    }
}
