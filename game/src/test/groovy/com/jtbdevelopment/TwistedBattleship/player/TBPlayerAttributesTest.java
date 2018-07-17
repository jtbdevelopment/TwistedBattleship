package com.jtbdevelopment.TwistedBattleship.player;

import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerPayLevel;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Date: 8/20/15
 * Time: 6:43 AM
 */
public class TBPlayerAttributesTest extends MongoGameCoreTestCase {
    @Test
    public void testSetTypePlayer() {
        Player p = MongoGameCoreTestCase.makeSimplePlayer(new ObjectId().toString());
        p.setPayLevel(PlayerPayLevel.FreeToPlay);
        TBPlayerAttributes attributes = new TBPlayerAttributes();

        attributes.setPlayer(p);
        assertEquals(50, attributes.getMaxDailyFreeGames());
        p.setPayLevel(PlayerPayLevel.PremiumPlayer);
        TBPlayerAttributes attributes1 = new TBPlayerAttributes();
        attributes1.setPlayer(p);
        assertEquals(100, attributes1.getMaxDailyFreeGames());
    }

    @Test
    public void testDefaults() {
        TBPlayerAttributes attributes = new TBPlayerAttributes();
        assertEquals("default-theme", attributes.getTheme());
        assertEquals(new HashSet<>(Arrays.asList("default-theme", "pirate-theme")), attributes.getAvailableThemes());

        assertEquals(0, attributes.getWins());
        assertEquals(0, attributes.getLosses());
        assertEquals(0, attributes.getCurrentWinStreak());
        assertEquals(0, attributes.getHighestScore());
    }

    @Test
    public void testAddsFreeThemesIfNotInSetIfSettingThemes() {
        TBPlayerAttributes attributes = new TBPlayerAttributes();
        attributes.setAvailableThemes(new HashSet<>(Arrays.asList("1", "2")));
        assertEquals(new HashSet<>(Arrays.asList("default-theme", "pirate-theme", "1", "2")), attributes.getAvailableThemes());
    }

}
