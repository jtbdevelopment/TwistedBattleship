package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.player.tracking.AbstractPlayerGameTrackingAttributes
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import groovy.transform.CompileStatic
import org.springframework.data.annotation.Transient

/**
 * Date: 1/30/15
 * Time: 6:34 PM
 */
@CompileStatic
class TBPlayerAttributes extends AbstractPlayerGameTrackingAttributes {
    private static final String DEFAULT_THEME = 'default-theme'
    public static final int DEFAULT_FREE_GAMES_PER_DAY = 20;
    public static final int DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY = 50;

    @Transient
    int maxDailyFreeGames

    String theme = DEFAULT_THEME
    List<String> availableThemes = [DEFAULT_THEME]

    //  TODO - title, win streak, max points etc

    @Transient
    @Override
    void setPlayer(final Player player) {
        super.setPlayer(player)
        maxDailyFreeGames = (player.payLevel == PlayerPayLevel.FreeToPlay ? DEFAULT_FREE_GAMES_PER_DAY : DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY)
    }
}
